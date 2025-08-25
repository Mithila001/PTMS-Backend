package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import com.tritonptms.public_transport_management_system.model.ActionLog;
import com.tritonptms.public_transport_management_system.model.ActionLog.ActionType;
import com.tritonptms.public_transport_management_system.repository.ActionLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;

    public ActionLogService(ActionLogRepository actionLogRepository) {
        this.actionLogRepository = actionLogRepository;
    }

    public void logAction(ActionType actionType, String entityType, Long entityId, String details) {
        // Get the current username from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymousUser";

        // Create a new ActionLog object
        ActionLog log = new ActionLog(username, actionType, entityType, entityId, details);

        // Save the log entry to the database
        actionLogRepository.save(log);
    }

    public List<ActionLog> getAllLogs() {
        return actionLogRepository.findAll();
    }

    public List<ActionLog> getLogsByEntityType(String entityType) {
        // return actionLogRepository.findByEntityTypeOrderByTimestampDesc(entityType);
        List<ActionLog> logs = actionLogRepository.findByEntityTypeOrderByTimestampDesc(entityType);
        if (logs.isEmpty()) {
            throw new ResourceNotFoundException("No logs found for entity type: " + entityType);
        }
        return logs;
    }

    public List<ActionLog> getLogsByUsernameAndEntityType(String username, String entityType) {
        List<ActionLog> logs = actionLogRepository.findByUsernameAndEntityTypeOrderByTimestampDesc(username,
                entityType);
        if (logs.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No logs found for username '" + username + "' and entity type '" + entityType + "'");
        }
        return logs;
    }
}