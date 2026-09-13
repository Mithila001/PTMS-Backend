package com.tritonptms.ptms.feature.employee.dto;

import java.time.LocalDate;

public record ConductorResponse(
        Long id,
        String nicNumber,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String contactNumber,
        String email,
        String address,
        LocalDate dateJoined,
        boolean currentEmployee,
        String conductorLicenseNumber,
        LocalDate licenseExpirationDate,
        boolean available) {
}
