package com.tritonptms.ptms.feature.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DriverRequest(
        @NotBlank @Pattern(regexp = "^([0-9]{9}[vV]|[0-9]{12})$") String nicNumber,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotNull @Past LocalDate dateOfBirth,
        @NotBlank @Pattern(regexp = "^[0-9]{10}$") String contactNumber,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(max = 255) String address,
        @NotNull @PastOrPresent LocalDate dateJoined,
        @NotNull Boolean currentEmployee,
        @NotBlank @Size(min = 8, max = 15) String drivingLicenseNumber,
        @NotNull LocalDate licenseExpirationDate,
        @NotBlank @Size(max = 30) String licenseClass,
        @Size(max = 15) String ntcLicenseNumber,
        LocalDate ntcLicenseExpirationDate,
        @NotNull Boolean available) {
}
