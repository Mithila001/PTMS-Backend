package com.tritonptms.ptms.feature.assignment;

import com.tritonptms.ptms.feature.assignment.dto.AssignmentRequest;
import com.tritonptms.ptms.feature.assignment.dto.AssignmentResponse;
import com.tritonptms.ptms.feature.bus.Bus;
import com.tritonptms.ptms.feature.bus.BusRepository;
import com.tritonptms.ptms.common.exception.BadRequestException;
import com.tritonptms.ptms.common.exception.ConflictException;
import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import com.tritonptms.ptms.feature.employee.Conductor;
import com.tritonptms.ptms.feature.employee.ConductorRepository;
import com.tritonptms.ptms.feature.employee.Driver;
import com.tritonptms.ptms.feature.employee.DriverRepository;
import com.tritonptms.ptms.feature.schedule.ScheduledTrip;
import com.tritonptms.ptms.feature.schedule.ScheduledTripRepository;
import com.tritonptms.ptms.feature.schedule.TripTime;
import com.tritonptms.ptms.infrastructure.persistence.SchedulingWriteLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
@PreAuthorize("hasAnyRole('ADMIN','OPERATIONS_MANAGER')")
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final ScheduledTripRepository scheduledTripRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final ConductorRepository conductorRepository;
    private final AssignmentMapper assignmentMapper;
    private final SchedulingWriteLock schedulingWriteLock;

    public AssignmentService(AssignmentRepository assignmentRepository,
            ScheduledTripRepository scheduledTripRepository,
            BusRepository busRepository,
            DriverRepository driverRepository,
            ConductorRepository conductorRepository,
            AssignmentMapper assignmentMapper,
            SchedulingWriteLock schedulingWriteLock) {
        this.assignmentRepository = assignmentRepository;
        this.scheduledTripRepository = scheduledTripRepository;
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.conductorRepository = conductorRepository;
        this.assignmentMapper = assignmentMapper;
        this.schedulingWriteLock = schedulingWriteLock;
    }

    public Page<AssignmentResponse> getAllAssignments(Pageable pageable) {
        return assignmentRepository.findAll(pageable).map(assignmentMapper::toResponse);
    }

    public AssignmentResponse getAssignmentById(Long id) {
        return assignmentMapper.toResponse(findAssignment(id));
    }

    @Transactional
    public AssignmentResponse createAssignment(AssignmentRequest request) {
        schedulingWriteLock.acquire();
        validateTimes(request);
        Assignment assignment = assignmentMapper.fromRequest(request);
        applyReferences(assignment, request);
        validateEligibility(assignment);
        validateNoConflicts(assignment, -1L);
        return assignmentMapper.toResponse(assignmentRepository.save(assignment));
    }

    @Transactional
    public AssignmentResponse updateAssignment(Long id, AssignmentRequest request) {
        schedulingWriteLock.acquire();
        validateTimes(request);
        Assignment assignment = findAssignment(id);
        assignmentMapper.apply(assignment, request);
        applyReferences(assignment, request);
        validateEligibility(assignment);
        validateNoConflicts(assignment, id);
        return assignmentMapper.toResponse(assignmentRepository.save(assignment));
    }

    @Transactional
    public void deleteAssignment(Long id) {
        schedulingWriteLock.acquire();
        assignmentRepository.delete(findAssignment(id));
    }

    public Page<AssignmentResponse> searchAssignments(Long scheduledTripId,
            Long busId,
            Long driverId,
            Long conductorId,
            LocalDate date,
            AssignmentStatus status,
            String driverName,
            String conductorName,
            Pageable pageable) {
        Specification<Assignment> specification = Specification.allOf(
                AssignmentSpecification.hasScheduledTripId(scheduledTripId),
                AssignmentSpecification.hasBusId(busId),
                AssignmentSpecification.hasDriverId(driverId),
                AssignmentSpecification.hasConductorId(conductorId),
                AssignmentSpecification.hasDate(date),
                AssignmentSpecification.hasStatus(status),
                AssignmentSpecification.driverNameContains(driverName),
                AssignmentSpecification.conductorNameContains(conductorName));
        return assignmentRepository.findAll(specification, pageable).map(assignmentMapper::toResponse);
    }

    private Assignment findAssignment(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
    }

    private void applyReferences(Assignment assignment, AssignmentRequest request) {
        ScheduledTrip trip = scheduledTripRepository.findById(request.scheduledTripId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Scheduled trip not found with id: " + request.scheduledTripId()));
        Bus bus = busRepository.findById(request.busId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + request.busId()));
        Driver driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + request.driverId()));
        Conductor conductor = conductorRepository.findById(request.conductorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conductor not found with id: " + request.conductorId()));

        assignment.setScheduledTrip(trip);
        assignment.setBus(bus);
        assignment.setDriver(driver);
        assignment.setConductor(conductor);
    }

    private void validateTimes(AssignmentRequest request) {
        switch (request.status()) {
            case SCHEDULED -> {
                if (request.actualStartTime() != null || request.actualEndTime() != null) {
                    throw new BadRequestException("Scheduled assignments cannot have actual times.");
                }
            }
            case IN_PROGRESS -> {
                if (request.actualStartTime() == null || request.actualEndTime() != null) {
                    throw new BadRequestException("In-progress assignments require a start time and no end time.");
                }
            }
            case COMPLETED -> {
                if (request.actualStartTime() == null || request.actualEndTime() == null
                        || !request.actualEndTime().isAfter(request.actualStartTime())) {
                    throw new BadRequestException("Completed assignments require an end time after the start time.");
                }
            }
            case CANCELED -> {
                if (request.actualEndTime() != null && (request.actualStartTime() == null
                        || !request.actualEndTime().isAfter(request.actualStartTime()))) {
                    throw new BadRequestException("An assignment end time must follow its start time.");
                }
            }
        }
        if (request.actualStartTime() != null && !request.actualStartTime().toLocalDate().equals(request.date())) {
            throw new BadRequestException("actualStartTime must be on the assignment date.");
        }
    }

    private void validateEligibility(Assignment assignment) {
        if (assignment.getStatus() == AssignmentStatus.CANCELED
                || assignment.getStatus() == AssignmentStatus.COMPLETED) {
            return;
        }
        LocalDate plannedEndDate = TripTime.interval(assignment.getDate(),
                assignment.getScheduledTrip().getExpectedStartTime(),
                assignment.getScheduledTrip().getExpectedEndTime()).end().toLocalDate();
        if (!assignment.getBus().isActive()
                || !assignment.getDriver().isAvailable() || !assignment.getDriver().getIsCurrentEmployee()
                || !assignment.getConductor().isAvailable() || !assignment.getConductor().getIsCurrentEmployee()) {
            throw new ConflictException("The bus and staff must be active, current employees, and available.");
        }
        if (asLocalDate(assignment.getDriver().getLicenseExpirationDate()).isBefore(plannedEndDate)
                || (assignment.getDriver().getNtcLicenseNumber() != null
                        && (assignment.getDriver().getNtcLicenseExpirationDate() == null
                                || asLocalDate(assignment.getDriver().getNtcLicenseExpirationDate())
                                        .isBefore(plannedEndDate)))
                || asLocalDate(assignment.getConductor().getLicenseExpirationDate()).isBefore(plannedEndDate)) {
            throw new ConflictException("Required staff licences must remain valid through the trip.");
        }
    }

    private void validateNoConflicts(Assignment assignment, Long excludeId) {
        if (assignment.getStatus() == AssignmentStatus.CANCELED) {
            return;
        }
        TripTime.Interval requested = TripTime.interval(assignment.getDate(),
                assignment.getScheduledTrip().getExpectedStartTime(),
                assignment.getScheduledTrip().getExpectedEndTime());
        for (Assignment existing : assignmentRepository.findBookingCandidates(
                assignment.getDate().minusDays(1), assignment.getDate().plusDays(1), excludeId,
                assignment.getScheduledTrip().getId(), assignment.getBus().getId(),
                assignment.getDriver().getId(), assignment.getConductor().getId())) {
            TripTime.Interval booked = TripTime.interval(existing.getDate(),
                    existing.getScheduledTrip().getExpectedStartTime(),
                    existing.getScheduledTrip().getExpectedEndTime());
            if (requested.overlaps(booked)) {
                throw new ConflictException("The trip overlaps an existing assignment for this bus, staff, or trip.");
            }
        }
    }

    private LocalDate asLocalDate(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }
}
