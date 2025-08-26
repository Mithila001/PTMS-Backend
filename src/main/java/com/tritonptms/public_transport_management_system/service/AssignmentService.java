package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Assignment;
import com.tritonptms.public_transport_management_system.dto.AssignmentDto;
import java.util.List;
import java.util.Optional;

public interface AssignmentService {
    List<AssignmentDto> getAllAssignments();

    Optional<AssignmentDto> getAssignmentById(Long id);

    Assignment saveAssignment(AssignmentDto assignmentDto);

    void deleteAssignment(Long id);

    AssignmentDto convertToDto(Assignment assignment);

    Assignment convertToEntity(AssignmentDto assignmentDto);
}