package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.dto.AssignmentDto;
import com.tritonptms.public_transport_management_system.model.*;
import com.tritonptms.public_transport_management_system.model.enums.assignment.AssignmentStatus;
import com.tritonptms.public_transport_management_system.repository.*;
import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final ConductorRepository conductorRepository;
    private final ScheduledTripService scheduledTripService;
    private final ScheduledTripRepository scheduledTripRepository;

    @Autowired
    public AssignmentServiceImpl(AssignmentRepository assignmentRepository, BusRepository busRepository,
            DriverRepository driverRepository, ConductorRepository conductorRepository,
            ScheduledTripService scheduledTripService, ScheduledTripRepository scheduledTripRepository) {
        this.assignmentRepository = assignmentRepository;
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.conductorRepository = conductorRepository;
        this.scheduledTripService = scheduledTripService;
        this.scheduledTripRepository = scheduledTripRepository;
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
        Assignment assignment = convertToEntity(assignmentDto);
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

        if (assignment.getScheduledTrip() != null) {
            dto.setScheduledTrip(scheduledTripService.convertToDto(assignment.getScheduledTrip()));
        }
        if (assignment.getBus() != null) {
            dto.setBusId(assignment.getBus().getId());
            dto.setBusRegistrationNumber(assignment.getBus().getRegistrationNumber());
        }
        if (assignment.getDriver() != null) {
            dto.setDriverId(assignment.getDriver().getId());
            dto.setDriverName(assignment.getDriver().getFirstName() + " " + assignment.getDriver().getLastName());
        }
        if (assignment.getConductor() != null) {
            dto.setConductorId(assignment.getConductor().getId());
            dto.setConductorName(
                    assignment.getConductor().getFirstName() + " " + assignment.getConductor().getLastName());
        }

        return dto;
    }

    @Override
    public Assignment convertToEntity(AssignmentDto assignmentDto) {
        Assignment entity = new Assignment();
        if (assignmentDto.getId() != null) {
            entity.setId(assignmentDto.getId());
        }
        entity.setDate(assignmentDto.getDate());
        entity.setActualStartTime(assignmentDto.getActualStartTime());
        entity.setActualEndTime(assignmentDto.getActualEndTime());
        entity.setStatus(assignmentDto.getStatus());

        if (assignmentDto.getScheduledTrip() != null && assignmentDto.getScheduledTrip().getId() != null) {
            ScheduledTrip scheduledTrip = scheduledTripRepository.findById(assignmentDto.getScheduledTrip().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "ScheduledTrip not found with id: " + assignmentDto.getScheduledTrip().getId()));
            entity.setScheduledTrip(scheduledTrip);
        }
        if (assignmentDto.getBusId() != null) {
            Bus bus = busRepository.findById(assignmentDto.getBusId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Bus not found with id: " + assignmentDto.getBusId()));
            entity.setBus(bus);
        }
        if (assignmentDto.getDriverId() != null) {
            Driver driver = driverRepository.findById(assignmentDto.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Driver not found with id: " + assignmentDto.getDriverId()));
            entity.setDriver(driver);
        }
        if (assignmentDto.getConductorId() != null) {
            Conductor conductor = conductorRepository.findById(assignmentDto.getConductorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Conductor not found with id: " + assignmentDto.getConductorId()));
            entity.setConductor(conductor);
        }

        return entity;
    }

    @Override
    public List<AssignmentDto> getAssignmentsByStatus(AssignmentStatus status) {
        return assignmentRepository.findByStatus(status)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}