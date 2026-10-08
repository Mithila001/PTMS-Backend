
package com.tritonptms.ptms.feature.dashboard;

import com.tritonptms.ptms.feature.dashboard.dto.DashboardDto;
import com.tritonptms.ptms.feature.assignment.AssignmentRepository;
import com.tritonptms.ptms.feature.bus.BusRepository;
import com.tritonptms.ptms.feature.employee.ConductorRepository;
import com.tritonptms.ptms.feature.employee.DriverRepository;
import com.tritonptms.ptms.feature.route.RouteRepository;
import com.tritonptms.ptms.feature.schedule.ScheduledTripRepository;

import java.time.LocalDate;
import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final ConductorRepository conductorRepository;
    private final RouteRepository routeRepository;
    private final ScheduledTripRepository scheduledTripRepository;
    private final AssignmentRepository assignmentRepository;
    private final Clock clock;

    public DashboardService(BusRepository busRepository, DriverRepository driverRepository,
            ConductorRepository conductorRepository, RouteRepository routeRepository,
            ScheduledTripRepository scheduledTripRepository, AssignmentRepository assignmentRepository, Clock clock) {
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.conductorRepository = conductorRepository;
        this.routeRepository = routeRepository;
        this.scheduledTripRepository = scheduledTripRepository;
        this.assignmentRepository = assignmentRepository;
        this.clock = clock;
    }
    public DashboardDto getDashboardData() {
        long totalActiveBuses = busRepository.countByIsActive(true);
        long totalActiveDrivers = driverRepository.countActiveEmployees();
        long totalActiveConductors = conductorRepository.countActiveEmployees();
        long totalRoutes = routeRepository.count();
        long totalScheduledTrips = scheduledTripRepository.count();
        long totalActiveAssignments = assignmentRepository.countDashboardActive(LocalDate.now(clock));

        return new DashboardDto(
                totalActiveBuses,
                totalActiveDrivers,
                totalActiveConductors,
                totalRoutes,
                totalScheduledTrips,
                totalActiveAssignments);
    }
}
