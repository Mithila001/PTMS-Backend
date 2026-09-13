package com.tritonptms.ptms.schedule.dto;

import com.tritonptms.ptms.route.dto.RouteResponse;
import com.tritonptms.ptms.schedule.Direction;

import java.time.LocalTime;

public record ScheduledTripResponse(
        Long id,
        RouteResponse route,
        Direction direction,
        LocalTime expectedStartTime,
        LocalTime expectedEndTime) {
}
