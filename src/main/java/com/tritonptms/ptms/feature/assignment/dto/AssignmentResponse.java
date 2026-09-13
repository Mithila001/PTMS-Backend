package com.tritonptms.ptms.feature.assignment.dto;

import com.tritonptms.ptms.feature.assignment.AssignmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AssignmentResponse(
        Long id,
        Long scheduledTripId,
        Long busId,
        String busRegistrationNumber,
        Long driverId,
        String driverName,
        Long conductorId,
        String conductorName,
        LocalDate date,
        LocalDateTime actualStartTime,
        LocalDateTime actualEndTime,
        AssignmentStatus status) {
}
