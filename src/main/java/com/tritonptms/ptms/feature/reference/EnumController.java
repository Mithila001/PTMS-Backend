package com.tritonptms.ptms.feature.reference;

import com.tritonptms.ptms.feature.bus.Bus.ComfortType;
import com.tritonptms.ptms.feature.bus.Bus.ServiceType;
import com.tritonptms.ptms.feature.bus.Bus.FuelType;
import com.tritonptms.ptms.feature.user.ERole;

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
