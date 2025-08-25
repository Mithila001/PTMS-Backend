package com.tritonptms.public_transport_management_system.service;

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
}