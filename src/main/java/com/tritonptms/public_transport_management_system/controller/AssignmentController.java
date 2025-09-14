package com.tritonptms.public_transport_management_system.controller;

import com.tritonptms.public_transport_management_system.model.Assignment;
import com.tritonptms.public_transport_management_system.model.enums.assignment.AssignmentStatus;
import com.tritonptms.public_transport_management_system.service.AssignmentService;
import com.tritonptms.public_transport_management_system.utils.BaseResponse;
import com.tritonptms.public_transport_management_system.dto.AssignmentDto;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<Page<AssignmentDto>>> searchAssignments(
            @RequestParam(required = false) Long scheduledTripId,
            @RequestParam(required = false) Long busId,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) Long conductorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AssignmentStatus status,
            @RequestParam(required = false) String driverName,
            @RequestParam(required = false) String conductorName,
            Pageable pageable) {

        Page<AssignmentDto> assignmentPage = assignmentService.searchAssignments(
                scheduledTripId,
                busId,
                driverId,
                conductorId,
                date,
                status,
                driverName,
                conductorName,
                pageable);

        return new ResponseEntity<>(BaseResponse.success(assignmentPage, "Assignments retrieved successfully."),
                HttpStatus.OK);
    }
}