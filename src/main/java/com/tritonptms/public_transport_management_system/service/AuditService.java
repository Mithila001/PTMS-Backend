package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.dto.AuditLogDto;
import com.tritonptms.public_transport_management_system.dto.ChangeDetailDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;

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
import java.util.HashMap;
import java.util.Arrays;

@Service
public class AuditService {

    private final EntityManager entityManager;
    private final ActionLogService actionLogService; // To log actions to central table

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
     * @param displayNameField The field name to use for display in summaries (e.g.,
     *                         "registrationNumber" for Bus)
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
            DefaultRevisionEntity revEntity = (DefaultRevisionEntity) revision[1];
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

            AuditLogDto log = new AuditLogDto(
                    Long.valueOf(revEntity.getId()),
                    new Date(revEntity.getTimestamp()),
                    entityName,
                    getEntityId(currentEntity),
                    revType.name(),
                    summary);
            log.setChanges(changes);
            auditLogs.add(log);

            // Log this action to the central ActionLog table
            actionLogService.saveAuditLogAction(log);

            previousEntity = currentEntity;
        }

        java.util.Collections.reverse(auditLogs);
        return auditLogs;
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

    /// ----------------------------------------------------------------------------------
    /// Helper Methods
    /// ----------------------------------------------------------------------------------

    /**
     * Get changes between two entity instances using reflection
     */
    private <T> List<ChangeDetailDto> getChanges(T previousEntity, T currentEntity, List<String> auditableFields) {
        List<ChangeDetailDto> changes = new ArrayList<>();

        try {
            DiffBuilder<T> diffBuilder = new DiffBuilder<>(previousEntity, currentEntity,
                    ToStringStyle.SHORT_PREFIX_STYLE);

            // Get all fields to compare
            List<String> fieldsToCompare = auditableFields != null ? auditableFields
                    : getAllFieldNames(currentEntity.getClass());

            for (String fieldName : fieldsToCompare) {
                try {
                    Object previousValue = getFieldValue(previousEntity, fieldName);
                    Object currentValue = getFieldValue(currentEntity, fieldName);
                    diffBuilder.append(fieldName, previousValue, currentValue);
                } catch (Exception e) {
                    // Skip fields that can't be accessed
                    continue;
                }
            }

            DiffResult<T> diff = diffBuilder.build();

            for (Object d : diff.getDiffs()) {
                Diff<Object> fieldDiff = (Diff<Object>) d;
                changes.add(new ChangeDetailDto(
                        fieldDiff.getFieldName(),
                        String.valueOf(fieldDiff.getLeft()),
                        String.valueOf(fieldDiff.getRight())));
            }
        } catch (Exception e) {
            // Log error but don't fail the entire operation
            System.err.println("Error getting changes for entity: " + e.getMessage());
        }

        return changes;
    }

    /**
     * Get the display name for an entity (e.g., registration number for bus)
     */
    private <T> String getDisplayName(T entity, String displayNameField) {
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
    private <T> Long getEntityId(T entity) {
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