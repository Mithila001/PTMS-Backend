package com.tritonptms.ptms.audit.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ActionLogResponse(
        Long id,
        Long userId,
        String entityType,
        Long entityId,
        String revisionType,
        String summary,
        List<ChangeDetailDto> changes,
        LocalDateTime timestamp) {
}
