package com.tritonptms.ptms.feature.dashboard;

import com.tritonptms.ptms.feature.assignment.AssignmentRepository;
import com.tritonptms.ptms.feature.bus.BusRepository;
import com.tritonptms.ptms.feature.employee.ConductorRepository;
import com.tritonptms.ptms.feature.employee.DriverRepository;
import com.tritonptms.ptms.feature.route.RouteRepository;
import com.tritonptms.ptms.feature.schedule.ScheduledTripRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    @Test
    void usesSriLankanCalendarDateForActiveAssignmentCount() {
        var buses = mock(BusRepository.class);
        var drivers = mock(DriverRepository.class);
        var conductors = mock(ConductorRepository.class);
        var routes = mock(RouteRepository.class);
        var trips = mock(ScheduledTripRepository.class);
        var assignments = mock(AssignmentRepository.class);
        var clock = Clock.fixed(Instant.parse("2026-10-08T20:00:00Z"), ZoneId.of("Asia/Colombo"));
        when(assignments.countDashboardActive(LocalDate.of(2026, 10, 9))).thenReturn(4L);

        var dto = new DashboardService(buses, drivers, conductors, routes, trips, assignments, clock)
                .getDashboardData();

        assertThat(dto.getTotalActiveAssignments()).isEqualTo(4);
        verify(assignments).countDashboardActive(LocalDate.of(2026, 10, 9));
    }
}
