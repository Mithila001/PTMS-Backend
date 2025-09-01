package com.tritonptms.public_transport_management_system.controller;

import com.tritonptms.public_transport_management_system.model.Conductor;
import com.tritonptms.public_transport_management_system.model.Driver;
import com.tritonptms.public_transport_management_system.model.Employee;
import com.tritonptms.public_transport_management_system.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchAllEmployees(
            @RequestParam(required = false) String nicNumber,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String contactNumber,
            @RequestParam(required = false) String licenseNumber) {

        List<Employee> employees = employeeService.searchEmployees(nicNumber, name, contactNumber, licenseNumber);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/drivers/search")
    public ResponseEntity<List<Driver>> searchDrivers(
            @RequestParam(required = false) String nicNumber,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String contactNumber,
            @RequestParam(required = false) String licenseNumber) {

        List<Driver> drivers = employeeService.searchDrivers(nicNumber, name, contactNumber, licenseNumber);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/conductors/search")
    public ResponseEntity<List<Conductor>> searchConductors(
            @RequestParam(required = false) String nicNumber,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String contactNumber,
            @RequestParam(required = false) String licenseNumber) {

        List<Conductor> conductors = employeeService.searchConductors(nicNumber, name, contactNumber, licenseNumber);
        return ResponseEntity.ok(conductors);
    }
}