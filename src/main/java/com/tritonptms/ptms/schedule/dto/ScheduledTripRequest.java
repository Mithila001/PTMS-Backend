package com.tritonptms.ptms.schedule.dto;

import com.tritonptms.ptms.schedule.Direction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;

public record ScheduledTripRequest(
        @NotNull @Positive Long routeId,
        @NotNull Direction direction,
        @NotNull LocalTime expectedStartTime,
        @NotNull LocalTime expectedEndTime) {
}
