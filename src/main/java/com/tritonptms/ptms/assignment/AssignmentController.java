package com.tritonptms.ptms.assignment;

import com.tritonptms.ptms.assignment.dto.AssignmentRequest;
import com.tritonptms.ptms.assignment.dto.AssignmentResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public List<AssignmentResponse> getAllAssignments() {
        return assignmentService.getAllAssignments();
    }

    @GetMapping("/{id}")
    public AssignmentResponse getAssignmentById(@PathVariable Long id) {
        return assignmentService.getAssignmentById(id);
    }

    @PostMapping
    public ResponseEntity<AssignmentResponse> createAssignment(@Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.createAssignment(request));
    }

    @PutMapping("/{id}")
    public AssignmentResponse updateAssignment(@PathVariable Long id,
            @Valid @RequestBody AssignmentRequest request) {
        return assignmentService.updateAssignment(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Page<AssignmentResponse> searchAssignments(
            @RequestParam(required = false) Long scheduledTripId,
            @RequestParam(required = false) Long busId,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) Long conductorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AssignmentStatus status,
            @RequestParam(required = false) String driverName,
            @RequestParam(required = false) String conductorName,
            Pageable pageable) {
        return assignmentService.searchAssignments(
                scheduledTripId, busId, driverId, conductorId, date, status, driverName, conductorName, pageable);
    }
}
