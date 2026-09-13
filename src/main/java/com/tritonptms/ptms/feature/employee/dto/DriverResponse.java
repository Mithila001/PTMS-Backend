package com.tritonptms.ptms.feature.employee.dto;

import java.time.LocalDate;

public record DriverResponse(
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
        String drivingLicenseNumber,
        LocalDate licenseExpirationDate,
        String licenseClass,
        String ntcLicenseNumber,
        LocalDate ntcLicenseExpirationDate,
        boolean available) {
}
