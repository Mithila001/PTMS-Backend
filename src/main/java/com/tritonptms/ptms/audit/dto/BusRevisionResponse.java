package com.tritonptms.ptms.audit.dto;

import com.tritonptms.ptms.bus.dto.BusResponse;

import java.time.Instant;

public record BusRevisionResponse(
        Integer revision,
        Instant timestamp,
        String modifiedBy,
        String type,
        BusResponse snapshot) {
}
