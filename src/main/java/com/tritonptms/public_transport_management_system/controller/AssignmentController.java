package com.tritonptms.public_transport_management_system.controller;

import com.tritonptms.public_transport_management_system.model.Assignment;
import com.tritonptms.public_transport_management_system.service.AssignmentService;
import com.tritonptms.public_transport_management_system.dto.AssignmentDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public ResponseEntity<List<AssignmentDto>> getAllAssignments() {
        List<AssignmentDto> assignments = assignmentService.getAllAssignments();
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentDto> getAssignmentById(@PathVariable Long id) {
        Optional<AssignmentDto> assignment = assignmentService.getAssignmentById(id);
        return assignment.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<AssignmentDto> createAssignment(@Valid @RequestBody AssignmentDto assignmentDto) {
        Assignment newAssignment = assignmentService.saveAssignment(assignmentDto);
        AssignmentDto newAssignmentDto = assignmentService.convertToDto(newAssignment);
        return new ResponseEntity<>(newAssignmentDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssignmentDto> updateAssignment(@PathVariable Long id,
            @Valid @RequestBody AssignmentDto assignmentDetailsDto) {
        assignmentDetailsDto.setId(id);
        Assignment updatedAssignment = assignmentService.saveAssignment(assignmentDetailsDto);
        AssignmentDto updatedAssignmentDto = assignmentService.convertToDto(updatedAssignment);
        return new ResponseEntity<>(updatedAssignmentDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}