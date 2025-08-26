package com.tritonptms.public_transport_management_system.dto;

import com.tritonptms.public_transport_management_system.model.enums.assignment.AssignmentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AssignmentDto {
    private Long id;

    @NotNull(message = "Scheduled trip is mandatory")
    private ScheduledTripDto scheduledTrip;

    @NotNull(message = "Bus ID is mandatory")
    private Long busId;

    @NotNull(message = "Driver ID is mandatory")
    private Long driverId;

    @NotNull(message = "Conductor ID is mandatory")
    private Long conductorId;

    @NotNull(message = "Date is mandatory")
    private LocalDate date;

    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;

    @NotNull(message = "Status is mandatory")
    private AssignmentStatus status;

    // Getters and Setters...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScheduledTripDto getScheduledTrip() {
        return scheduledTrip;
    }

    public void setScheduledTrip(ScheduledTripDto scheduledTrip) {
        this.scheduledTrip = scheduledTrip;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getConductorId() {
        return conductorId;
    }

    public void setConductorId(Long conductorId) {
        this.conductorId = conductorId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public LocalDateTime getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(LocalDateTime actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }
}