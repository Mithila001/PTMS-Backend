package com.tritonptms.ptms.audit;

import com.tritonptms.ptms.audit.dto.ActionLogResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/action-logs")
public class ActionLogController {

    private final ActionLogService actionLogService;

    public ActionLogController(ActionLogService actionLogService) {
        this.actionLogService = actionLogService;
    }

    @GetMapping
    public List<ActionLogResponse> getAllActionLogs() {
        return actionLogService.getAllActionLogs();
    }
}
