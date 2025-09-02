// F:\OnGoinProject\Transport Management System\public-transport-management-system\src\main\java\com\tritonptms\public_transport_management_system\service\AssignmentServiceImpl.java

package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.dto.AssignmentDto;
import com.tritonptms.public_transport_management_system.model.*;
import com.tritonptms.public_transport_management_system.model.enums.assignment.AssignmentStatus;
import com.tritonptms.public_transport_management_system.repository.*;
import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final ScheduledTripRepository scheduledTripRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final ConductorRepository conductorRepository;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository,
            ScheduledTripRepository scheduledTripRepository,
            BusRepository busRepository,
            DriverRepository driverRepository,
            ConductorRepository conductorRepository) {
        this.assignmentRepository = assignmentRepository;
        this.scheduledTripRepository = scheduledTripRepository;
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.conductorRepository = conductorRepository;
    }

    @Override
    public List<AssignmentDto> getAllAssignments() {
        return assignmentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AssignmentDto> getAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public Assignment saveAssignment(AssignmentDto assignmentDto) {
        Assignment assignment;
        if (assignmentDto.getId() != null) {
            assignment = assignmentRepository.findById(assignmentDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Assignment not found with id: " + assignmentDto.getId()));
            updateEntityFromDto(assignment, assignmentDto);
        } else {
            assignment = convertToEntity(assignmentDto);
        }
        return assignmentRepository.save(assignment);
    }

    @Override
    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }

    @Override
    public AssignmentDto convertToDto(Assignment assignment) {
        AssignmentDto dto = new AssignmentDto();
        dto.setId(assignment.getId());
        dto.setDate(assignment.getDate());
        dto.setActualStartTime(assignment.getActualStartTime());
        dto.setActualEndTime(assignment.getActualEndTime());
        dto.setStatus(assignment.getStatus());
        dto.setScheduledTripId(assignment.getScheduledTrip().getId());
        dto.setBusId(assignment.getBus().getId());
        dto.setBusRegistrationNumber(assignment.getBus().getRegistrationNumber());
        dto.setDriverId(assignment.getDriver().getId());
        dto.setDriverName(assignment.getDriver().getFirstName() + " " + assignment.getDriver().getLastName());
        dto.setConductorId(assignment.getConductor().getId());
        dto.setConductorName(
                assignment.getConductor().getFirstName() + " " + assignment.getConductor().getLastName());

        return dto;
    }

    @Override
    public Assignment convertToEntity(AssignmentDto assignmentDto) {
        Assignment entity = new Assignment();
        if (assignmentDto.getId() != null) {
            entity.setId(assignmentDto.getId());
        }

        // This is where we use the IDs to find the entities.
        ScheduledTrip scheduledTrip = scheduledTripRepository.findById(assignmentDto.getScheduledTripId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ScheduledTrip not found with id: " + assignmentDto.getScheduledTripId()));

        Bus bus = busRepository.findById(assignmentDto.getBusId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Bus not found with id: " + assignmentDto.getBusId()));

        Driver driver = driverRepository.findById(assignmentDto.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver not found with id: " + assignmentDto.getDriverId()));

        Conductor conductor = conductorRepository.findById(assignmentDto.getConductorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conductor not found with id: " + assignmentDto.getConductorId()));

        entity.setScheduledTrip(scheduledTrip);
        entity.setBus(bus);
        entity.setDriver(driver);
        entity.setConductor(conductor);
        entity.setDate(assignmentDto.getDate());
        entity.setActualStartTime(assignmentDto.getActualStartTime());
        entity.setActualEndTime(assignmentDto.getActualEndTime());
        entity.setStatus(assignmentDto.getStatus());

        return entity;
    }

    private void updateEntityFromDto(Assignment entity, AssignmentDto dto) {
        // Fetch related entities using their IDs only if they are being updated
        if (dto.getScheduledTripId() != null) {
            ScheduledTrip scheduledTrip = scheduledTripRepository.findById(dto.getScheduledTripId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "ScheduledTrip not found with id: " + dto.getScheduledTripId()));
            entity.setScheduledTrip(scheduledTrip);
        }

        if (dto.getBusId() != null) {
            Bus bus = busRepository.findById(dto.getBusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + dto.getBusId()));
            entity.setBus(bus);
        }

        if (dto.getDriverId() != null) {
            Driver driver = driverRepository.findById(dto.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + dto.getDriverId()));
            entity.setDriver(driver);
        }

        if (dto.getConductorId() != null) {
            Conductor conductor = conductorRepository.findById(dto.getConductorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Conductor not found with id: " + dto.getConductorId()));
            entity.setConductor(conductor);
        }

        entity.setDate(dto.getDate());
        entity.setActualStartTime(dto.getActualStartTime());
        entity.setActualEndTime(dto.getActualEndTime());
        entity.setStatus(dto.getStatus());
    }

    @Override
    public List<AssignmentDto> getAssignmentsByStatus(AssignmentStatus status) {
        return assignmentRepository.findByStatus(status)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}