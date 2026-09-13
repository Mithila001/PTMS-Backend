package com.tritonptms.ptms.employee.dto;

import java.time.LocalDate;

public record EmployeeSummaryResponse(
        Long id,
        String employeeType,
        String nicNumber,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String contactNumber,
        String email,
        String address,
        LocalDate dateJoined,
        boolean currentEmployee,
        String licenseNumber,
        boolean available) {
}
