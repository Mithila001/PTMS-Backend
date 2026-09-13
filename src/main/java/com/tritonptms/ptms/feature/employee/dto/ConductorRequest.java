package com.tritonptms.ptms.feature.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ConductorRequest(
        @NotBlank @Pattern(regexp = "^([0-9]{9}[vV]|[0-9]{12})$") String nicNumber,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotNull @Past LocalDate dateOfBirth,
        @NotBlank @Pattern(regexp = "^[0-9]{10}$") String contactNumber,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(max = 255) String address,
        @NotNull @PastOrPresent LocalDate dateJoined,
        @NotNull Boolean currentEmployee,
        @NotBlank @Size(min = 8, max = 15) String conductorLicenseNumber,
        @NotNull LocalDate licenseExpirationDate,
        @NotNull Boolean available) {
}
