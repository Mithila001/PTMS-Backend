
package com.tritonptms.ptms.dashboard;

import com.tritonptms.ptms.dashboard.dto.DashboardDto;
import com.tritonptms.ptms.common.web.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<BaseResponse<DashboardDto>> getDashboardMetrics() {
        DashboardDto dashboardData = dashboardService.getDashboardData();
        return ResponseEntity.ok(BaseResponse.success(dashboardData, "Dashboard metrics fetched successfully."));
    }
}
