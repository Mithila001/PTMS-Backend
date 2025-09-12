package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.auditing.CustomRevisionEntity;
import com.tritonptms.public_transport_management_system.dto.AuditLogDto;
import com.tritonptms.public_transport_management_system.dto.ChangeDetailDto;
import com.tritonptms.public_transport_management_system.model.Bus;
import com.tritonptms.public_transport_management_system.model.Driver;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.Query;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.criteria.AuditCriterion;

import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.builder.Diff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Arrays;

@Service
public class AuditService {

    private final EntityManager entityManager;
    private final ActionLogService actionLogService;

    // List of audited entity classes - add your audited entities here
    private final List<Class<?>> auditedEntityClasses = Arrays.asList(
            Bus.class,
            Driver.class
    // Add other audited entity classes here as needed
    );

    @Autowired
    public AuditService(EntityManager entityManager, ActionLogService actionLogService) {
        this.entityManager = entityManager;
        this.actionLogService = actionLogService;
    }

    /**
     * Generic method to get audit logs for any entity
     *
     * @param entityClass      The class of the entity (e.g., Bus.class,
     *                         Driver.class)
     * @param entityId         The ID of the entity
     * @param displayNameField The field name to use for display in summaries
     * @param auditableFields  List of field names to track for changes (null means
     *                         track all fields)
     * @return List of audit logs
     */
    @SuppressWarnings("unchecked")
    public <T> List<AuditLogDto> getAuditLogsForEntity(Class<T> entityClass, Long entityId,
            String displayNameField, List<String> auditableFields) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        // Get all revisions for the specified entity
        List<Object[]> revisions = auditReader.createQuery()
                .forRevisionsOfEntity(entityClass, false, true)
                .add(AuditEntity.id().eq(entityId))
                .addOrder(AuditEntity.revisionNumber().asc())
                .getResultList();

        List<AuditLogDto> auditLogs = new ArrayList<>();
        T previousEntity = null;

        for (Object[] revision : revisions) {
            T currentEntity = (T) revision[0];
            CustomRevisionEntity revEntity = (CustomRevisionEntity) revision[1];
            RevisionType revType = (RevisionType) revision[2];

            String entityName = entityClass.getSimpleName();
            String displayName = getDisplayName(currentEntity, displayNameField);
            String summary;
            List<ChangeDetailDto> changes = new ArrayList<>();

            switch (revType) {
                case ADD:
                    summary = entityName + " '" + displayName + "' was created.";
                    break;
                case MOD:
                    summary = entityName + " '" + displayName + "' was updated.";
                    if (previousEntity != null) {
                        changes = getChanges(previousEntity, currentEntity, auditableFields);
                    }
                    break;
                case DEL:
                    summary = entityName + " '" + displayName + "' was deleted.";
                    break;
                default:
                    summary = "Unknown action.";
                    break;
            }

            AuditLogDto log = new AuditLogDto();
            log.setRevisionId(Long.valueOf(revEntity.getId()));
            log.setTimestamp(new Date(revEntity.getTimestamp()));
            log.setEntityType(entityName);
            log.setEntityId(getEntityId(currentEntity));
            log.setRevisionType(revType.name());
            log.setSummary(summary);
            log.setChanges(changes);
            log.setUsername(revEntity.getUsername());
            auditLogs.add(log);

            previousEntity = currentEntity;
        }

        java.util.Collections.reverse(auditLogs);
        return auditLogs;
    }

    /**
     * Gets a list of recent audit logs across all audited entities.
     * Fixed version that properly queries revision entities.
     *
     * @param limit The maximum number of recent logs to retrieve.
     * @return A list of recent audit logs.
     */
    @SuppressWarnings("unchecked")
    public List<AuditLogDto> getRecentGlobalAuditLogs(int limit) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<AuditLogDto> globalLogs = new ArrayList<>();

        try {
            // Method 1: Use native query to get recent revisions
            Query query = entityManager.createNativeQuery(
                    "SELECT id FROM custom_revision_entity ORDER BY id DESC LIMIT ?1");
            query.setParameter(1, limit * auditedEntityClasses.size());

            List<Number> recentRevisionIds = query.getResultList();

            for (Number revisionId : recentRevisionIds) {
                // For each revision, check all audited entity types
                for (Class<?> entityClass : auditedEntityClasses) {
                    try {
                        List<Object[]> entityRevisions = auditReader.createQuery()
                                .forRevisionsOfEntity(entityClass, false, true)
                                .add(AuditEntity.revisionNumber().eq(revisionId))
                                .getResultList();

                        for (Object[] revisionData : entityRevisions) {
                            Object entity = revisionData[0];
                            CustomRevisionEntity revEntity = (CustomRevisionEntity) revisionData[1];
                            RevisionType revType = (RevisionType) revisionData[2];

                            AuditLogDto log = createAuditLogDtoFromRevision(entity, entityClass, revEntity, revType,
                                    auditReader);
                            globalLogs.add(log);
                        }
                    } catch (Exception e) {
                        // Skip entities that might have issues
                        System.err.println("Error processing entity " + entityClass.getSimpleName() + " for revision "
                                + revisionId + ": " + e.getMessage());
                    }
                }

                // Stop if we have enough logs
                if (globalLogs.size() >= limit) {
                    break;
                }
            }

            // Sort by revision ID descending and limit results
            globalLogs.sort((a, b) -> b.getRevisionId().compareTo(a.getRevisionId()));
            if (globalLogs.size() > limit) {
                globalLogs = globalLogs.subList(0, limit);
            }

        } catch (Exception e) {
            System.err.println("Error in getRecentGlobalAuditLogs: " + e.getMessage());
            e.printStackTrace();

            // Fallback method: Query each entity type individually
            return getRecentGlobalAuditLogsFallback(limit);
        }

        return globalLogs;
    }

    /**
     * Fallback method for getting recent audit logs.
     * This method queries each audited entity type individually.
     */
    @SuppressWarnings("unchecked")
    private List<AuditLogDto> getRecentGlobalAuditLogsFallback(int limit) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<AuditLogDto> globalLogs = new ArrayList<>();

        for (Class<?> entityClass : auditedEntityClasses) {
            try {
                List<Object[]> revisions = auditReader.createQuery()
                        .forRevisionsOfEntity(entityClass, false, true)
                        .addOrder(AuditEntity.revisionNumber().desc())
                        .setMaxResults(limit / auditedEntityClasses.size() + 1)
                        .getResultList();

                for (Object[] revisionData : revisions) {
                    Object entity = revisionData[0];
                    CustomRevisionEntity revEntity = (CustomRevisionEntity) revisionData[1];
                    RevisionType revType = (RevisionType) revisionData[2];

                    AuditLogDto log = createAuditLogDtoFromRevision(entity, entityClass, revEntity, revType,
                            auditReader);
                    globalLogs.add(log);
                }
            } catch (Exception e) {
                System.err.println("Error processing entity " + entityClass.getSimpleName() + ": " + e.getMessage());
            }
        }

        // Sort by revision ID descending and limit results
        globalLogs.sort((a, b) -> b.getRevisionId().compareTo(a.getRevisionId()));
        if (globalLogs.size() > limit) {
            globalLogs = globalLogs.subList(0, limit);
        }

        return globalLogs;
    }

    /**
     * Convenience method for Bus entities
     */
    public List<AuditLogDto> getAuditLogsForBus(Long busId) {
        return getAuditLogsForEntity(
                com.tritonptms.public_transport_management_system.model.Bus.class,
                busId,
                "registrationNumber",
                Arrays.asList("registrationNumber", "make", "model", "capacity", "status"));
    }

    /**
     * Example convenience method for Driver entities
     */
    public List<AuditLogDto> getAuditLogsForDriver(Long driverId) {
        return getAuditLogsForEntity(
                com.tritonptms.public_transport_management_system.model.Driver.class,
                driverId,
                "name",
                Arrays.asList("name", "licenseNumber", "phoneNumber", "email", "status"));
    }

    // ----------------------------------------------------------------------------------
    // Helper Methods
    // ----------------------------------------------------------------------------------

    /**
     * Helper method to create an AuditLogDto from a given revision.
     */
    private <T> AuditLogDto createAuditLogDtoFromRevision(Object entity, Class<T> entityClass,
            CustomRevisionEntity revEntity, RevisionType revType, AuditReader auditReader) {

        Number revisionId = revEntity.getId();

        // Get the previous version of the entity for comparison
        Object previousEntity = null;
        if (revType == RevisionType.MOD) {
            try {
                // Get all revisions for this specific entity to find the actual previous
                // version
                List<Number> revisions = auditReader.getRevisions(entityClass, getEntityId(entity));

                // Find the previous revision for this entity
                Number previousRevisionId = null;
                for (int i = 0; i < revisions.size(); i++) {
                    if (revisions.get(i).equals(revisionId) && i > 0) {
                        previousRevisionId = revisions.get(i - 1);
                        break;
                    }
                }

                if (previousRevisionId != null) {
                    previousEntity = auditReader.find(entityClass, getEntityId(entity), previousRevisionId);
                }
            } catch (Exception e) {
                // Previous entity might not exist, that's okay
                System.err.println("Could not find previous entity for comparison: " + e.getMessage());
            }
        }

        String summary;
        List<ChangeDetailDto> changes = new ArrayList<>();
        String entityName = entityClass.getSimpleName();
        String displayNameField = getDisplayNameField(entityClass);
        String displayName = getDisplayName(entity, displayNameField);

        switch (revType) {
            case ADD:
                summary = entityName + " '" + displayName + "' was created.";
                break;
            case MOD:
                summary = entityName + " '" + displayName + "' was updated.";
                if (previousEntity != null) {
                    changes = getChanges(previousEntity, entity, getAllFieldNames(entityClass));
                }
                break;
            case DEL:
                // For deletions, try to get the entity state from the previous revision
                try {
                    List<Number> revisions = auditReader.getRevisions(entityClass, getEntityId(entity));
                    Number previousRevisionId = null;
                    for (int i = 0; i < revisions.size(); i++) {
                        if (revisions.get(i).equals(revisionId) && i > 0) {
                            previousRevisionId = revisions.get(i - 1);
                            break;
                        }
                    }

                    if (previousRevisionId != null) {
                        Object deletedEntityState = auditReader.find(entityClass, getEntityId(entity),
                                previousRevisionId);
                        if (deletedEntityState != null) {
                            String deletedDisplayName = getDisplayName(deletedEntityState,
                                    getDisplayNameField(entityClass));
                            summary = entityName + " '" + deletedDisplayName + "' was deleted.";
                        } else {
                            summary = entityName + " '" + displayName + "' was deleted.";
                        }
                    } else {
                        summary = entityName + " '" + displayName + "' was deleted.";
                    }
                } catch (Exception e) {
                    summary = entityName + " '" + displayName + "' was deleted.";
                }
                break;
            default:
                summary = "Unknown action.";
                break;
        }

        AuditLogDto log = new AuditLogDto();
        log.setRevisionId(Long.valueOf(revisionId.longValue()));
        log.setTimestamp(new Date(revEntity.getTimestamp()));
        log.setEntityType(entityName);
        log.setEntityId(getEntityId(entity));
        log.setRevisionType(revType.name());
        log.setSummary(summary);
        log.setChanges(changes);
        log.setUsername(revEntity.getUsername());
        return log;
    }

    /**
     * Determines the display name field for a given entity class.
     */
    private String getDisplayNameField(Class<?> entityClass) {
        if (entityClass.equals(Bus.class)) {
            return "registrationNumber";
        }
        if (entityClass.equals(Driver.class)) {
            return "name";
        }
        // Add other entity mappings here
        // For example:
        // if (entityClass.equals(Route.class)) {
        // return "name";
        // }
        // if (entityClass.equals(Conductor.class)) {
        // return "name";
        // }

        // Fallback
        return "id";
    }

    /**
     * Get changes between two entity instances using reflection
     */
    private <T> List<ChangeDetailDto> getChanges(Object previousEntity, Object currentEntity,
            List<String> auditableFields) {
        List<ChangeDetailDto> changes = new ArrayList<>();

        try {
            System.out.println("Comparing entities: " + previousEntity.getClass().getSimpleName());
            System.out.println("Fields to compare: " + auditableFields);

            DiffBuilder diffBuilder = new DiffBuilder(previousEntity, currentEntity,
                    ToStringStyle.SHORT_PREFIX_STYLE);

            // Get all fields to compare
            List<String> fieldsToCompare = auditableFields != null ? auditableFields
                    : getAllFieldNames(currentEntity.getClass());

            for (String fieldName : fieldsToCompare) {
                try {
                    Object previousValue = getFieldValue(previousEntity, fieldName);
                    Object currentValue = getFieldValue(currentEntity, fieldName);

                    System.out.println(
                            "Field: " + fieldName + ", Previous: " + previousValue + ", Current: " + currentValue);

                    diffBuilder.append(fieldName, previousValue, currentValue);
                } catch (Exception e) {
                    System.err.println("Error accessing field " + fieldName + ": " + e.getMessage());
                    // Skip fields that can't be accessed
                    continue;
                }
            }

            DiffResult diff = diffBuilder.build();
            System.out.println("Diff result has " + diff.getNumberOfDiffs() + " differences");

            for (Object d : diff.getDiffs()) {
                Diff<Object> fieldDiff = (Diff<Object>) d;
                changes.add(new ChangeDetailDto(
                        fieldDiff.getFieldName(),
                        String.valueOf(fieldDiff.getLeft()),
                        String.valueOf(fieldDiff.getRight())));
                System.out.println("Added change: " + fieldDiff.getFieldName() +
                        " from " + fieldDiff.getLeft() + " to " + fieldDiff.getRight());
            }
        } catch (Exception e) {
            // Log error but don't fail the entire operation
            System.err.println("Error getting changes for entity: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Final changes count: " + changes.size());
        return changes;
    }

    /**
     * Get the display name for an entity (e.g., registration number for bus)
     */
    private String getDisplayName(Object entity, String displayNameField) {
        try {
            Object value = getFieldValue(entity, displayNameField);
            return value != null ? value.toString() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * Get the ID of an entity using reflection to find @Id annotated field
     */
    private Long getEntityId(Object entity) {
        if (entity == null) {
            return null;
        }

        Class<?> currentClass = entity.getClass();
        while (currentClass != null && currentClass != Object.class) {
            try {
                Field[] fields = currentClass.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Id.class)) {
                        field.setAccessible(true);
                        Object value = field.get(entity);
                        if (value instanceof Long) {
                            return (Long) value;
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore, try superclass
            }
            currentClass = currentClass.getSuperclass();
        }

        // Fallback to trying a method named getId() if no @Id field is found
        try {
            Method getIdMethod = entity.getClass().getMethod("getId");
            Object value = getIdMethod.invoke(entity);
            if (value instanceof Long) {
                return (Long) value;
            }
        } catch (Exception ex) {
            // Ignore, as no standard ID access method exists
        }
        return null;
    }

    /**
     * Get field value using getter method or direct field access
     */
    private Object getFieldValue(Object entity, String fieldName) throws Exception {
        // Try getter method first
        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            Method getter = entity.getClass().getMethod(getterName);
            return getter.invoke(entity);
        } catch (NoSuchMethodException e) {
            // Try boolean getter
            String booleanGetterName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            try {
                Method booleanGetter = entity.getClass().getMethod(booleanGetterName);
                return booleanGetter.invoke(entity);
            } catch (NoSuchMethodException ex) {
                // Try direct field access
                Field field = entity.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(entity);
            }
        }
    }

    /**
     * Get all field names from a class (excluding static and transient fields)
     */
    private List<String> getAllFieldNames(Class<?> entityClass) {
        List<String> fieldNames = new ArrayList<>();
        Field[] fields = entityClass.getDeclaredFields();

        for (Field field : fields) {
            // Skip static, transient, and synthetic fields
            if (!java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                    !java.lang.reflect.Modifier.isTransient(field.getModifiers()) &&
                    !field.isSynthetic()) {
                fieldNames.add(field.getName());
            }
        }
        return fieldNames;
    }
}