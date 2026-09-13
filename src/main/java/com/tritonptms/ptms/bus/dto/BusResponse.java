package com.tritonptms.ptms.bus.dto;

import com.tritonptms.ptms.bus.Bus.ComfortType;
import com.tritonptms.ptms.bus.Bus.FuelType;
import com.tritonptms.ptms.bus.Bus.ServiceType;

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
