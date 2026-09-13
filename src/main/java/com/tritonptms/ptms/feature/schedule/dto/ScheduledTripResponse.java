package com.tritonptms.ptms.feature.schedule.dto;

import com.tritonptms.ptms.feature.route.dto.RouteResponse;
import com.tritonptms.ptms.feature.schedule.Direction;

import java.time.LocalTime;

public record ScheduledTripResponse(
        Long id,
        RouteResponse route,
        Direction direction,
        LocalTime expectedStartTime,
        LocalTime expectedEndTime) {
}
