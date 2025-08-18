package com.tritonptms.public_transport_management_system.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import jakarta.validation.constraints.Size;
import org.locationtech.jts.geom.LineString;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Route number is mandatory")
    @Size(max = 20, message = "Route number cannot exceed 20 characters")
    @Column(nullable = false, unique = true)
    private String routeNumber; // e.g., "177", "100"

    @NotBlank(message = "Origin is mandatory")
    @Size(max = 100, message = "Origin cannot exceed 100 characters")
    @Column(nullable = false)
    private String origin; // e.g., "Kaduwela"

    @NotBlank(message = "Destination is mandatory")
    @Size(max = 100, message = "Destination cannot exceed 100 characters")
    @Column(nullable = false)
    private String destination; // e.g., "Colombo Fort"

    @ElementCollection
    @CollectionTable(name = "route_stops", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "stop_name")
    private List<String> majorStops; // List of key stops along the route

    @Column(columnDefinition = "geography(LineString,4326)")
    private LineString routePath;

    public Route() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<String> getMajorStops() {
        return majorStops;
    }

    public void setMajorStops(List<String> majorStops) {
        this.majorStops = majorStops;
    }

    public LineString getRoutePath() {
        return routePath;
    }

    public void setRoutePath(LineString routePath) {
        this.routePath = routePath;
    }
}