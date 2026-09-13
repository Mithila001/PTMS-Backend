package com.tritonptms.ptms.employee;

import com.tritonptms.ptms.employee.dto.EmployeeSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<EmployeeSummaryResponse> searchDrivers(
            @RequestParam(required = false) String nicNumber,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String contactNumber,
            @RequestParam(required = false) String licenseNumber,
            Pageable pageable) {
        return employeeService.searchDrivers(nicNumber, name, contactNumber, licenseNumber, pageable);
    }

    @GetMapping("/conductors/search")
    public Page<EmployeeSummaryResponse> searchConductors(
            @RequestParam(required = false) String nicNumber,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String contactNumber,
            @RequestParam(required = false) String licenseNumber,
            Pageable pageable) {
        return employeeService.searchConductors(nicNumber, name, contactNumber, licenseNumber, pageable);
    }
}
