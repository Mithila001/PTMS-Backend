// src/main/java/com/tritonptms/public_transport_management_system/dto/DashboardDto.java

package com.tritonptms.public_transport_management_system.dto;

public class DashboardDto {
    private long totalActiveBuses;
    private long totalActiveDrivers;
    private long totalActiveConductors;
    private long totalRoutes;
    private long totalScheduledTrips;
    private long totalActiveAssignments;

    public DashboardDto() {
    }

    public DashboardDto(long totalActiveBuses, long totalActiveDrivers, long totalActiveConductors, long totalRoutes,
            long totalScheduledTrips, long totalActiveAssignments) {
        this.totalActiveBuses = totalActiveBuses;
        this.totalActiveDrivers = totalActiveDrivers;
        this.totalActiveConductors = totalActiveConductors;
        this.totalRoutes = totalRoutes;
        this.totalScheduledTrips = totalScheduledTrips;
        this.totalActiveAssignments = totalActiveAssignments;
    }

    public long getTotalActiveBuses() {
        return totalActiveBuses;
    }

    public void setTotalActiveBuses(long totalActiveBuses) {
        this.totalActiveBuses = totalActiveBuses;
    }

    public long getTotalActiveDrivers() {
        return totalActiveDrivers;
    }

    public void setTotalActiveDrivers(long totalActiveDrivers) {
        this.totalActiveDrivers = totalActiveDrivers;
    }

    public long getTotalActiveConductors() {
        return totalActiveConductors;
    }

    public void setTotalActiveConductors(long totalActiveConductors) {
        this.totalActiveConductors = totalActiveConductors;
    }

    public long getTotalRoutes() {
        return totalRoutes;
    }

    public void setTotalRoutes(long totalRoutes) {
        this.totalRoutes = totalRoutes;
    }

    public long getTotalScheduledTrips() {
        return totalScheduledTrips;
    }

    public void setTotalScheduledTrips(long totalScheduledTrips) {
        this.totalScheduledTrips = totalScheduledTrips;
    }

    public long getTotalActiveAssignments() {
        return totalActiveAssignments;
    }

    public void setTotalActiveAssignments(long totalActiveAssignments) {
        this.totalActiveAssignments = totalActiveAssignments;
    }
}