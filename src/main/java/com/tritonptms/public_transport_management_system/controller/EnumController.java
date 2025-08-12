package com.tritonptms.public_transport_management_system.controller;

import com.tritonptms.public_transport_management_system.model.enums.bus.BusType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping("/bus-types")
    public List<BusType> getBusTypes() {
        return Arrays.asList(BusType.values());
    }
}
