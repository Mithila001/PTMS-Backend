package com.tritonptms.public_transport_management_system.controller;

import com.tritonptms.public_transport_management_system.dto.EmployeeDto;
import com.tritonptms.public_transport_management_system.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/drivers/search")
    public ResponseEntity<Page<EmployeeDto>> searchDrivers(
            @RequestParam(required = false) String nicNumber,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String contactNumber,
            @RequestParam(required = false) String licenseNumber,
            Pageable pageable) {

        Page<EmployeeDto> driversPage = employeeService.searchDrivers(
                nicNumber, name, contactNumber, licenseNumber, pageable);
        return ResponseEntity.ok(driversPage);
    }

    @GetMapping("/conductors/search")
    public ResponseEntity<Page<EmployeeDto>> searchConductors(
            @RequestParam(required = false) String nicNumber,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String contactNumber,
            @RequestParam(required = false) String licenseNumber,
            Pageable pageable) {

        Page<EmployeeDto> conductorsPage = employeeService.searchConductors(
                nicNumber, name, contactNumber, licenseNumber, pageable);
        return ResponseEntity.ok(conductorsPage);
    }
}