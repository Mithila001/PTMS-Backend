package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.dto.AuditLogDto;
import com.tritonptms.public_transport_management_system.dto.ChangeDetailDto;
import com.tritonptms.public_transport_management_system.model.Bus;

import jakarta.persistence.EntityManager;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AuditService {

    private final EntityManager entityManager;

    @Autowired
    public AuditService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unchecked")
    public List<AuditLogDto> getAuditLogsForBus(Long busId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        // Get all revisions for the specified bus in a single query
        List<Object[]> revisions = auditReader.createQuery()
                .forRevisionsOfEntity(Bus.class, false, true)
                .add(AuditEntity.id().eq(busId))
                .addOrder(AuditEntity.revisionNumber().asc())
                .getResultList();

        List<AuditLogDto> auditLogs = new ArrayList<>();
        Bus previousBus = null;

        for (Object[] revision : revisions) {
            Bus currentBus = (Bus) revision[0];
            DefaultRevisionEntity revEntity = (DefaultRevisionEntity) revision[1];
            RevisionType revType = (RevisionType) revision[2];

            String summary;
            List<ChangeDetailDto> changes = new ArrayList<>();

            switch (revType) {
                case ADD:
                    summary = "Bus '" + currentBus.getRegistrationNumber() + "' was created.";
                    break;
                case MOD:
                    summary = "Bus '" + currentBus.getRegistrationNumber() + "' was updated.";
                    if (previousBus != null) {
                        DiffResult<Bus> diff = new DiffBuilder<>(previousBus, currentBus,
                                ToStringStyle.SHORT_PREFIX_STYLE)
                                .append("registrationNumber", previousBus.getRegistrationNumber(),
                                        currentBus.getRegistrationNumber())
                                .append("make", previousBus.getMake(), currentBus.getMake())
                                .append("model", previousBus.getModel(), currentBus.getModel())
                                .build();

                        // Use a traditional for-each loop to safely cast each Diff object
                        for (Object d : diff.getDiffs()) {
                            Diff<Object> fieldDiff = (Diff<Object>) d;
                            changes.add(new ChangeDetailDto(
                                    fieldDiff.getFieldName(),
                                    String.valueOf(fieldDiff.getLeft()),
                                    String.valueOf(fieldDiff.getRight())));
                        }
                    }
                    break;
                case DEL:
                    summary = "Bus '" + currentBus.getRegistrationNumber() + "' was deleted.";
                    break;
                default:
                    summary = "Unknown action.";
                    break;
            }

            AuditLogDto log = new AuditLogDto(
                    Long.valueOf(revEntity.getId()),
                    new Date(revEntity.getTimestamp()),
                    "Bus",
                    currentBus.getId(),
                    revType.name(),
                    summary);
            log.setChanges(changes);
            auditLogs.add(log);

            previousBus = currentBus;
        }

        java.util.Collections.reverse(auditLogs);
        return auditLogs;
    }
}