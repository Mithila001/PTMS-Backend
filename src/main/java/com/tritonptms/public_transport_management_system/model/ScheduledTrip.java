package com.tritonptms.public_transport_management_system.model;

import com.tritonptms.public_transport_management_system.model.enums.route.Direction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@Entity
@Table(name = "scheduled_trips")
public class ScheduledTrip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @NotNull(message = "Direction is mandatory")
    private Direction direction;

    @Column(nullable = false)
    private LocalTime expectedStartTime;

    @Column(nullable = false)
    private LocalTime expectedEndTime;

    public ScheduledTrip() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
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