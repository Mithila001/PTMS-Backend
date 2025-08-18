package com.tritonptms.public_transport_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class RouteDto {
    private Long id; // Add this line

    @NotBlank(message = "Route number cannot be blank.")
    private String routeNumber;

    @NotBlank(message = "Origin cannot be blank.")
    @Size(max = 255, message = "Origin must be less than 255 characters.")
    private String origin;

    @NotBlank(message = "Destination cannot be blank.")
    @Size(max = 255, message = "Destination must be less than 255 characters.")
    private String destination;

    private List<String> majorStops;

    private String routePath;

    // Getters and Setters for all fields, including the new 'id' field
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

    public String getRoutePath() {
        return routePath;
    }

    public void setRoutePath(String routePath) {
        this.routePath = routePath;
    }
}