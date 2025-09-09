package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.dto.AuditLogDto;
import com.tritonptms.public_transport_management_system.model.ActionLog;
import com.tritonptms.public_transport_management_system.repository.ActionLogRepository;
import com.tritonptms.public_transport_management_system.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;

@Service
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;

    @Autowired
    public ActionLogService(ActionLogRepository actionLogRepository) {
        this.actionLogRepository = actionLogRepository;
    }

    /**
     * Saves a new action log entry from an audit log DTO.
     * This method gets the authenticated user's ID and saves the log to the central
     * table.
     */
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
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return user.getId();
        }
        return 4L; // Return 4 if the principal is not a User object DEV Only
    }
}