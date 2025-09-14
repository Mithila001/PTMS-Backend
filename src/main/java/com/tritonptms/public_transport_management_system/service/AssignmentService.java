package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Assignment;
import com.tritonptms.public_transport_management_system.model.enums.assignment.AssignmentStatus;
import com.tritonptms.public_transport_management_system.dto.AssignmentDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssignmentService {
    List<AssignmentDto> getAllAssignments();

    Optional<AssignmentDto> getAssignmentById(Long id);

    Assignment saveAssignment(AssignmentDto assignmentDto);

    void deleteAssignment(Long id);

    AssignmentDto convertToDto(Assignment assignment);

    Assignment convertToEntity(AssignmentDto assignmentDto);

    Page<AssignmentDto> searchAssignments(Long scheduledTripId, Long busId, Long driverId, Long conductorId,
            LocalDate date, AssignmentStatus status, String driverName, String conductorName, Pageable pageable);
}