package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.dto.EmployeeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    Page<EmployeeDto> searchDrivers(String nicNumber, String name, String contactNumber, String licenseNumber,
            Pageable pageable);

    Page<EmployeeDto> searchConductors(String nicNumber, String name, String contactNumber, String licenseNumber,
            Pageable pageable);
}