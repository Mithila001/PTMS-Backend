package com.tritonptms.ptms.feature.bus.audit.dto;

import com.tritonptms.ptms.feature.bus.dto.BusResponse;

import java.time.Instant;

public record BusRevisionResponse(
        Integer revision,
        Instant timestamp,
        String modifiedBy,
        String type,
        BusResponse snapshot) {
}
