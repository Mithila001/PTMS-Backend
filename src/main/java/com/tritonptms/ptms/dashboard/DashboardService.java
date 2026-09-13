
package com.tritonptms.ptms.dashboard;

import com.tritonptms.ptms.dashboard.dto.DashboardDto;
import com.tritonptms.ptms.assignment.AssignmentRepository;
import com.tritonptms.ptms.bus.BusRepository;
import com.tritonptms.ptms.employee.ConductorRepository;
import com.tritonptms.ptms.employee.DriverRepository;
import com.tritonptms.ptms.route.RouteRepository;
import com.tritonptms.ptms.schedule.ScheduledTripRepository;

import java.time.LocalDate;

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

    public DashboardService(BusRepository busRepository, DriverRepository driverRepository,
            ConductorRepository conductorRepository, RouteRepository routeRepository,
            ScheduledTripRepository scheduledTripRepository, AssignmentRepository assignmentRepository) {
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.conductorRepository = conductorRepository;
        this.routeRepository = routeRepository;
        this.scheduledTripRepository = scheduledTripRepository;
        this.assignmentRepository = assignmentRepository;
    }
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
