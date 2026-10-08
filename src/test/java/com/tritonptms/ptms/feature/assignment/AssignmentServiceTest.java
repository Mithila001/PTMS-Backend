package com.tritonptms.ptms.feature.assignment;

import com.tritonptms.ptms.common.exception.BadRequestException;
import com.tritonptms.ptms.common.exception.ConflictException;
import com.tritonptms.ptms.feature.assignment.dto.AssignmentRequest;
import com.tritonptms.ptms.feature.bus.Bus;
import com.tritonptms.ptms.feature.bus.BusRepository;
import com.tritonptms.ptms.feature.employee.Conductor;
import com.tritonptms.ptms.feature.employee.ConductorRepository;
import com.tritonptms.ptms.feature.employee.Driver;
import com.tritonptms.ptms.feature.employee.DriverRepository;
import com.tritonptms.ptms.feature.schedule.ScheduledTrip;
import com.tritonptms.ptms.feature.schedule.ScheduledTripRepository;
import com.tritonptms.ptms.infrastructure.persistence.SchedulingWriteLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock AssignmentRepository assignmentRepository;
    @Mock ScheduledTripRepository scheduledTripRepository;
    @Mock BusRepository busRepository;
    @Mock DriverRepository driverRepository;
    @Mock ConductorRepository conductorRepository;
    @Mock SchedulingWriteLock schedulingWriteLock;

    private AssignmentService service;
    private ScheduledTrip trip;
    private Bus bus;
    private Driver driver;
    private Conductor conductor;

    @BeforeEach
    void setUp() {
        service = new AssignmentService(assignmentRepository, scheduledTripRepository, busRepository,
                driverRepository, conductorRepository, new AssignmentMapper(), schedulingWriteLock);
        trip = new ScheduledTrip();
        trip.setId(1L);
        trip.setExpectedStartTime(LocalTime.of(8, 0));
        trip.setExpectedEndTime(LocalTime.of(9, 0));
        bus = new Bus();
        bus.setId(1L);
        bus.setActive(true);
        bus.setRegistrationNumber("ND-1001");
        driver = new Driver();
        driver.setId(1L);
        driver.setAvailable(true);
        driver.setIsCurrentEmployee(true);
        driver.setLicenseExpirationDate(Date.valueOf("2030-01-01"));
        conductor = new Conductor();
        conductor.setId(1L);
        conductor.setAvailable(true);
        conductor.setIsCurrentEmployee(true);
        conductor.setLicenseExpirationDate(Date.valueOf("2030-01-01"));
        when(scheduledTripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        lenient().when(assignmentRepository.findBookingCandidates(any(), any(), any(), anyLong(), anyLong(), anyLong(), anyLong()))
                .thenReturn(List.of());
        lenient().when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void rejectsOverlappingBooking() {
        when(assignmentRepository.findBookingCandidates(any(), any(), any(), anyLong(), anyLong(), anyLong(), anyLong()))
                .thenReturn(List.of(existing(LocalDate.of(2026, 10, 8), LocalTime.of(8, 30), LocalTime.of(9, 30))));

        assertThatThrownBy(() -> service.createAssignment(request(AssignmentStatus.SCHEDULED)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("overlaps");
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void allowsAdjacentBooking() {
        LocalDate date = LocalDate.of(2026, 10, 8);
        when(assignmentRepository.findBookingCandidates(any(), any(), any(), anyLong(), anyLong(), anyLong(), anyLong()))
                .thenReturn(List.of(existing(date, LocalTime.of(7, 0), LocalTime.of(8, 0))));
        service.createAssignment(request(AssignmentStatus.SCHEDULED));
        verify(assignmentRepository).findBookingCandidates(eq(date.minusDays(1)), eq(date.plusDays(1)), eq(-1L),
                eq(1L), eq(1L), eq(1L), eq(1L));
    }

    @Test
    void excludesTheAssignmentBeingUpdatedFromConflictChecks() {
        LocalDate date = LocalDate.of(2026, 10, 8);
        when(assignmentRepository.findById(2L)).thenReturn(Optional.of(existing(date,
                LocalTime.of(8, 0), LocalTime.of(9, 0))));
        service.updateAssignment(2L, request(AssignmentStatus.SCHEDULED));
        verify(assignmentRepository).findBookingCandidates(eq(date.minusDays(1)), eq(date.plusDays(1)), eq(2L),
                eq(1L), eq(1L), eq(1L), eq(1L));
    }

    @Test
    void rejectsUnavailableResourcesAndInconsistentStatusTimes() {
        bus.setActive(false);
        assertThatThrownBy(() -> service.createAssignment(request(AssignmentStatus.SCHEDULED)))
                .isInstanceOf(ConflictException.class);

        assertThatThrownBy(() -> service.createAssignment(new AssignmentRequest(1L, 1L, 1L, 1L,
                LocalDate.of(2026, 10, 8), null, null, AssignmentStatus.COMPLETED)))
                .isInstanceOf(BadRequestException.class);
    }

    private AssignmentRequest request(AssignmentStatus status) {
        return new AssignmentRequest(1L, 1L, 1L, 1L, LocalDate.of(2026, 10, 8), null, null, status);
    }

    private Assignment existing(LocalDate date, LocalTime start, LocalTime end) {
        ScheduledTrip existingTrip = new ScheduledTrip();
        existingTrip.setId(2L);
        existingTrip.setExpectedStartTime(start);
        existingTrip.setExpectedEndTime(end);
        Assignment assignment = new Assignment();
        assignment.setId(2L);
        assignment.setDate(date);
        assignment.setStatus(AssignmentStatus.SCHEDULED);
        assignment.setScheduledTrip(existingTrip);
        assignment.setBus(bus);
        assignment.setDriver(driver);
        assignment.setConductor(conductor);
        return assignment;
    }
}
