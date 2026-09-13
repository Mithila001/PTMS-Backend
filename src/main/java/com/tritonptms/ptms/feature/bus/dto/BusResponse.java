package com.tritonptms.ptms.feature.bus.dto;

import com.tritonptms.ptms.feature.bus.Bus.ComfortType;
import com.tritonptms.ptms.feature.bus.Bus.FuelType;
import com.tritonptms.ptms.feature.bus.Bus.ServiceType;

public record BusResponse(
        Long id,
        String registrationNumber,
        String make,
        String model,
        int yearOfManufacture,
        FuelType fuelType,
        boolean active,
        Integer seatingCapacity,
        Integer standingCapacity,
        Long ntcPermitNumber,
        ComfortType comfortType,
        Boolean airConditioned,
        ServiceType serviceType) {
}
