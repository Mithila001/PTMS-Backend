// src/main/java/com/tritonptms/public_transport_management_system/service/DashboardServiceImpl.java

package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.dto.DashboardDto;
import com.tritonptms.public_transport_management_system.repository.*;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final ConductorRepository conductorRepository;
    private final RouteRepository routeRepository;
    private final ScheduledTripRepository scheduledTripRepository;
    private final AssignmentRepository assignmentRepository;

    public DashboardServiceImpl(BusRepository busRepository, DriverRepository driverRepository,
            ConductorRepository conductorRepository, RouteRepository routeRepository,
            ScheduledTripRepository scheduledTripRepository, AssignmentRepository assignmentRepository) {
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.conductorRepository = conductorRepository;
        this.routeRepository = routeRepository;
        this.scheduledTripRepository = scheduledTripRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public DashboardDto getDashboardData() {
        long totalActiveBuses = busRepository.countByIsActive(true);
        long totalActiveDrivers = driverRepository.countByAvailable(true);
        long totalActiveConductors = conductorRepository.countByAvailable(true);
        long totalRoutes = routeRepository.count();
        long totalScheduledTrips = scheduledTripRepository.count();
        long totalActiveAssignments = assignmentRepository.countByDateAfter(LocalDate.now());

        return new DashboardDto(
                totalActiveBuses,
                totalActiveDrivers,
                totalActiveConductors,
                totalRoutes,
                totalScheduledTrips,
                totalActiveAssignments);
    }
}