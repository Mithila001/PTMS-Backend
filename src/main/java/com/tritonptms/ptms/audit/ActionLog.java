package com.tritonptms.ptms.audit;

import com.tritonptms.ptms.audit.dto.ChangeDetailDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "action_logs", indexes = {
        @Index(name = "idx_action_logs_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_action_logs_timestamp", columnList = "timestamp")
})
@EntityListeners(AuditingEntityListener.class)
public class ActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", updatable = false)
    private Long userId;

    @Column(name = "entity_type", nullable = false, updatable = false, length = 100)
    private String entityType;

    @Column(name = "entity_id", updatable = false)
    private Long entityId;

    @Column(name = "revision_type", nullable = false, updatable = false, length = 20)
    private String revisionType;

    @Column(nullable = false, updatable = false, length = 500)
    private String summary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<ChangeDetailDto> changes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public ActionLog() {
    }

    public ActionLog(Long userId, String entityType, Long entityId, String revisionType, String summary,
            List<ChangeDetailDto> changes) {
        this.userId = userId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.revisionType = revisionType;
        this.summary = summary;
        this.changes = changes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public String getRevisionType() { return revisionType; }
    public void setRevisionType(String revisionType) { this.revisionType = revisionType; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<ChangeDetailDto> getChanges() { return changes; }
    public void setChanges(List<ChangeDetailDto> changes) { this.changes = changes; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
