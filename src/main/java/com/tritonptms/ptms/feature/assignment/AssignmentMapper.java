package com.tritonptms.ptms.feature.assignment;

import com.tritonptms.ptms.feature.assignment.dto.AssignmentRequest;
import com.tritonptms.ptms.feature.assignment.dto.AssignmentResponse;
import org.springframework.stereotype.Component;

@Component
public class AssignmentMapper {

    public Assignment fromRequest(AssignmentRequest request) {
        Assignment assignment = new Assignment();
        apply(assignment, request);
        return assignment;
    }

    public void apply(Assignment assignment, AssignmentRequest request) {
        assignment.setDate(request.date());
        assignment.setActualStartTime(request.actualStartTime());
        assignment.setActualEndTime(request.actualEndTime());
        assignment.setStatus(request.status());
    }

    public AssignmentResponse toResponse(Assignment assignment) {
        return new AssignmentResponse(
                assignment.getId(),
                assignment.getScheduledTrip().getId(),
                assignment.getBus().getId(),
                assignment.getBus().getRegistrationNumber(),
                assignment.getDriver().getId(),
                fullName(assignment.getDriver().getFirstName(), assignment.getDriver().getLastName()),
                assignment.getConductor().getId(),
                fullName(assignment.getConductor().getFirstName(), assignment.getConductor().getLastName()),
                assignment.getDate(),
                assignment.getActualStartTime(),
                assignment.getActualEndTime(),
                assignment.getStatus());
    }

    private String fullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }
}
