package com.tritonptms.ptms.bus.dto;

import com.tritonptms.ptms.bus.Bus.ComfortType;
import com.tritonptms.ptms.bus.Bus.FuelType;
import com.tritonptms.ptms.bus.Bus.ServiceType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BusRequest(
        @NotBlank @Size(max = 15) String registrationNumber,
        @NotBlank @Size(max = 50) String make,
        @NotBlank @Size(max = 50) String model,
        @Min(1900) @Max(2100) int yearOfManufacture,
        @NotNull FuelType fuelType,
        @NotNull Boolean active,
        @NotNull @Min(1) @Max(100) Integer seatingCapacity,
        @NotNull @Min(0) @Max(50) Integer standingCapacity,
        @NotNull @Min(1000000000L) Long ntcPermitNumber,
        @NotNull ComfortType comfortType,
        @NotNull Boolean airConditioned,
        @NotNull ServiceType serviceType) {
}
