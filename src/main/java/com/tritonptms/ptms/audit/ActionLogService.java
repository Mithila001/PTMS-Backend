package com.tritonptms.ptms.audit;

import com.tritonptms.ptms.audit.dto.AuditLogDto;
import com.tritonptms.ptms.audit.dto.ActionLogResponse;
import com.tritonptms.ptms.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Service
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;
    public ActionLogService(ActionLogRepository actionLogRepository) {
        this.actionLogRepository = actionLogRepository;
    }


    @Transactional(readOnly = true)
    public List<ActionLogResponse> getAllActionLogs() {
        return actionLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp")).stream()
                .map(log -> new ActionLogResponse(
                        log.getId(),
                        log.getUserId(),
                        log.getEntityType(),
                        log.getEntityId(),
                        log.getRevisionType(),
                        log.getSummary(),
                        log.getChanges(),
                        log.getTimestamp()))
                .toList();
    }

    /**
     * Saves a new action log entry from an audit log DTO.
     * This method gets the authenticated user's ID and saves the log to the central
     * table.
     */
    @Transactional
    public void saveAuditLogAction(AuditLogDto auditLogDto) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            // Optional: Handle case where user is not logged in.
            // For now, we will save with a null userId.
        }

        ActionLog actionLog = new ActionLog(
                userId,
                auditLogDto.getEntityType(),
                auditLogDto.getEntityId(),
                auditLogDto.getRevisionType(),
                auditLogDto.getSummary(),
                auditLogDto.getChanges());

        // Use the timestamp from the audit log, converting it to LocalDateTime
        if (auditLogDto.getTimestamp() != null) {
            actionLog.setTimestamp(Instant.ofEpochMilli(auditLogDto.getTimestamp().getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        }

        actionLogRepository.save(actionLog);
    }

    /**
     * Retrieves the ID of the currently authenticated user.
     * This method correctly casts the principal to your custom User model
     * to access the ID field.
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser user) {
            return user.id();
        }
        return null;
    }
}
