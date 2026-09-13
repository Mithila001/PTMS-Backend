package com.tritonptms.ptms.assignment;

import com.tritonptms.ptms.assignment.dto.AssignmentRequest;
import com.tritonptms.ptms.assignment.dto.AssignmentResponse;
import com.tritonptms.ptms.bus.Bus;
import com.tritonptms.ptms.bus.BusRepository;
import com.tritonptms.ptms.common.exception.BadRequestException;
import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import com.tritonptms.ptms.employee.Conductor;
import com.tritonptms.ptms.employee.ConductorRepository;
import com.tritonptms.ptms.employee.Driver;
import com.tritonptms.ptms.employee.DriverRepository;
import com.tritonptms.ptms.schedule.ScheduledTrip;
import com.tritonptms.ptms.schedule.ScheduledTripRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    public AssignmentService(AssignmentRepository assignmentRepository,
            ScheduledTripRepository scheduledTripRepository,
            BusRepository busRepository,
            DriverRepository driverRepository,
            ConductorRepository conductorRepository,
            AssignmentMapper assignmentMapper) {
        this.assignmentRepository = assignmentRepository;
        this.scheduledTripRepository = scheduledTripRepository;
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.conductorRepository = conductorRepository;
        this.assignmentMapper = assignmentMapper;
    }

    public List<AssignmentResponse> getAllAssignments() {
        return assignmentRepository.findAll().stream().map(assignmentMapper::toResponse).toList();
    }

    public AssignmentResponse getAssignmentById(Long id) {
        return assignmentMapper.toResponse(findAssignment(id));
    }

    @Transactional
    public AssignmentResponse createAssignment(AssignmentRequest request) {
        validateTimes(request);
        Assignment assignment = assignmentMapper.fromRequest(request);
        applyReferences(assignment, request);
        return assignmentMapper.toResponse(assignmentRepository.save(assignment));
    }

    @Transactional
    public AssignmentResponse updateAssignment(Long id, AssignmentRequest request) {
        validateTimes(request);
        Assignment assignment = findAssignment(id);
        assignmentMapper.apply(assignment, request);
        applyReferences(assignment, request);
        return assignmentMapper.toResponse(assignmentRepository.save(assignment));
    }

    @Transactional
    public void deleteAssignment(Long id) {
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
        if (request.actualStartTime() != null && request.actualEndTime() != null
                && request.actualEndTime().isBefore(request.actualStartTime())) {
            throw new BadRequestException("actualEndTime cannot be before actualStartTime.");
        }
    }
}
