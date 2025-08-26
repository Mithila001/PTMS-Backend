package com.tritonptms.public_transport_management_system.dto;

import com.tritonptms.public_transport_management_system.model.enums.route.Direction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class ScheduledTripDto {
    private Long id;

    @NotNull(message = "Route is mandatory")
    private RouteDto route;

    @NotNull(message = "Direction is mandatory")
    private Direction direction;

    @NotNull(message = "Expected start time is mandatory")
    private LocalTime expectedStartTime;

    @NotNull(message = "Expected end time is mandatory")
    private LocalTime expectedEndTime;

    // Getters and Setters...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RouteDto getRoute() {
        return route;
    }

    public void setRoute(RouteDto route) {
        this.route = route;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public LocalTime getExpectedStartTime() {
        return expectedStartTime;
    }

    public void setExpectedStartTime(LocalTime expectedStartTime) {
        this.expectedStartTime = expectedStartTime;
    }

    public LocalTime getExpectedEndTime() {
        return expectedEndTime;
    }

    public void setExpectedEndTime(LocalTime expectedEndTime) {
        this.expectedEndTime = expectedEndTime;
    }
}