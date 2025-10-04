package com.tritonptms.public_transport_management_system.controller;

import com.tritonptms.public_transport_management_system.model.ActionLog;
import com.tritonptms.public_transport_management_system.repository.ActionLogRepository;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/action-logs")
public class ActionLogController {

    private final ActionLogRepository actionLogRepository;

    public ActionLogController(ActionLogRepository actionLogRepository) {
        this.actionLogRepository = actionLogRepository;
    }

    /**
     * Retrieves all action logs from the central table, ordered by timestamp 1.
     */
    @GetMapping("/all")
    public List<ActionLog> getAllActionLogs() {
        return actionLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }
}