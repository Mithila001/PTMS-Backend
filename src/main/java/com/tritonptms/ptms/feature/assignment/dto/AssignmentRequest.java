package com.tritonptms.ptms.feature.assignment.dto;

import com.tritonptms.ptms.feature.assignment.AssignmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AssignmentRequest(
        @NotNull @Positive Long scheduledTripId,
        @NotNull @Positive Long busId,
        @NotNull @Positive Long driverId,
        @NotNull @Positive Long conductorId,
        @NotNull LocalDate date,
        LocalDateTime actualStartTime,
        LocalDateTime actualEndTime,
        @NotNull AssignmentStatus status) {
}
