package com.tritonptms.public_transport_management_system.dto;

import java.util.Date;
import java.util.List;

public class AuditLogDto {

    private Long revisionId;
    private Date timestamp;
    private String entityType;
    private Long entityId;
    private String revisionType;
    private String summary; // A brief description of the change
    private List<ChangeDetailDto> changes;

    public AuditLogDto() {
    }

    public AuditLogDto(Long revisionId, Date timestamp, String entityType, Long entityId, String revisionType,
            String summary) {
        this.revisionId = revisionId;
        this.timestamp = timestamp;
        this.entityType = entityType;
        this.entityId = entityId;
        this.revisionType = revisionType;
        this.summary = summary;
    }

    // Getters and Setters
    public Long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(Long revisionId) {
        this.revisionId = revisionId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getRevisionType() {
        return revisionType;
    }

    public void setRevisionType(String revisionType) {
        this.revisionType = revisionType;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<ChangeDetailDto> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangeDetailDto> changes) {
        this.changes = changes;
    }
}