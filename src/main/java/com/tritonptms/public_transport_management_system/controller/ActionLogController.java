package com.tritonptms.public_transport_management_system.controller;

import com.tritonptms.public_transport_management_system.model.ActionLog;
import com.tritonptms.public_transport_management_system.service.ActionLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class ActionLogController {

    private final ActionLogService actionLogService;

    public ActionLogController(ActionLogService actionLogService) {
        this.actionLogService = actionLogService;
    }

    @GetMapping
    public ResponseEntity<List<ActionLog>> getAllLogs() {
        List<ActionLog> logs = actionLogService.getAllLogs();
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
}