package com.tritonptms.public_transport_management_system.controller;

import com.tritonptms.public_transport_management_system.dto.AuditLogDto;
import com.tritonptms.public_transport_management_system.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;

    @Autowired
    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/buses/{busId}")
    public ResponseEntity<List<AuditLogDto>> getBusAuditHistory(@PathVariable Long busId) {
        List<AuditLogDto> logs = auditService.getAuditLogsForBus(busId);
        return ResponseEntity.ok(logs);
    }
}