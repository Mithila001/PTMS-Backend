package com.tritonptms.ptms.feature.bus.audit;

import com.tritonptms.ptms.feature.bus.audit.dto.BusRevisionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/buses/{busId}/revisions")
public class BusAuditController {

    private final BusAuditService busAuditService;

    public BusAuditController(BusAuditService busAuditService) {
        this.busAuditService = busAuditService;
    }

    @GetMapping
    public List<BusRevisionResponse> getBusRevisions(@PathVariable Long busId) {
        return busAuditService.getRevisions(busId);
    }
}
