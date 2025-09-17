package com.tritonptms.public_transport_management_system.controller;

import com.tritonptms.public_transport_management_system.model.Bus.ComfortType;
import com.tritonptms.public_transport_management_system.model.Bus.ServiceType;
import com.tritonptms.public_transport_management_system.model.Vehicle.FuelType;
import com.tritonptms.public_transport_management_system.model.enums.bus.BusType;
import com.tritonptms.public_transport_management_system.model.enums.users.ERole;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping("/bus-enum-serviceTypes")
    public List<ServiceType> getBusEnumServiceTypes() {
        return Arrays.asList(ServiceType.values());
    }

    @GetMapping("/bus-enum-comfortTypes")
    public List<ComfortType> getBusEnumComfortTypes() {
        return Arrays.asList(ComfortType.values());
    }

    @GetMapping("/bus-enum-fuelTypes")
    public List<FuelType> getBusEnumFuelTypes() {
        return Arrays.asList(FuelType.values());
    }

    @GetMapping("/user-enum-roles")
    public List<ERole> getUserEnumRoles() {
        return Arrays.asList(ERole.values());
    }
}
