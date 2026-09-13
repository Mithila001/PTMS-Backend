package com.tritonptms.ptms.schedule;

import com.tritonptms.ptms.common.audit.AuditableEntity;
import com.tritonptms.ptms.route.Route;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Entity
@Table(name = "scheduled_trips", indexes = {
        @Index(name = "idx_scheduled_trips_route", columnList = "route_id"),
        @Index(name = "idx_scheduled_trips_direction", columnList = "direction")
})
public class ScheduledTrip extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @NotNull(message = "Direction is mandatory")
    private Direction direction;

    @Column(name = "expected_start_time", nullable = false)
    private LocalTime expectedStartTime;

    @Column(name = "expected_end_time", nullable = false)
    private LocalTime expectedEndTime;

    public ScheduledTrip() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }
    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public LocalTime getExpectedStartTime() { return expectedStartTime; }
    public void setExpectedStartTime(LocalTime expectedStartTime) { this.expectedStartTime = expectedStartTime; }
    public LocalTime getExpectedEndTime() { return expectedEndTime; }
    public void setExpectedEndTime(LocalTime expectedEndTime) { this.expectedEndTime = expectedEndTime; }
}
