package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.dto.EmployeeDto;
import com.tritonptms.public_transport_management_system.model.Conductor;
import com.tritonptms.public_transport_management_system.model.Driver;
import com.tritonptms.public_transport_management_system.repository.ConductorRepository;
import com.tritonptms.public_transport_management_system.repository.DriverRepository;
import com.tritonptms.public_transport_management_system.service.specification.EmployeeSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final DriverRepository driverRepository;
    private final ConductorRepository conductorRepository;

    public EmployeeServiceImpl(DriverRepository driverRepository, ConductorRepository conductorRepository) {
        this.driverRepository = driverRepository;
        this.conductorRepository = conductorRepository;
    }

    @Override
    public Page<EmployeeDto> searchDrivers(String nicNumber, String name, String contactNumber, String licenseNumber,
            Pageable pageable) {
        Specification<Driver> spec = EmployeeSpecification.forDrivers(nicNumber, name, contactNumber, licenseNumber);
        Page<Driver> driversPage = driverRepository.findAll(spec, pageable);
        return driversPage.map(EmployeeDto::fromEntity);
    }

    @Override
    public Page<EmployeeDto> searchConductors(String nicNumber, String name, String contactNumber, String licenseNumber,
            Pageable pageable) {
        Specification<Conductor> spec = EmployeeSpecification.forConductors(nicNumber, name, contactNumber,
                licenseNumber);
        Page<Conductor> conductorsPage = conductorRepository.findAll(spec, pageable);
        return conductorsPage.map(EmployeeDto::fromEntity);
    }
}