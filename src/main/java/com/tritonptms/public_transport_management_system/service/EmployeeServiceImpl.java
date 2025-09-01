package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Conductor;
import com.tritonptms.public_transport_management_system.model.Driver;
import com.tritonptms.public_transport_management_system.model.Employee;
import com.tritonptms.public_transport_management_system.repository.ConductorRepository;
import com.tritonptms.public_transport_management_system.repository.DriverRepository;
import com.tritonptms.public_transport_management_system.service.specification.EmployeeSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final DriverRepository driverRepository;
    private final ConductorRepository conductorRepository;

    public EmployeeServiceImpl(DriverRepository driverRepository, ConductorRepository conductorRepository) {
        this.driverRepository = driverRepository;
        this.conductorRepository = conductorRepository;
    }

    @Override
    public List<Employee> searchEmployees(String nicNumber, String name, String contactNumber, String licenseNumber) {
        List<Employee> employees = new ArrayList<>();

        // Search for drivers
        Specification<Driver> driverSpec = EmployeeSpecification.forDrivers(nicNumber, name, contactNumber,
                licenseNumber);
        employees.addAll(driverRepository.findAll(driverSpec));

        // Search for conductors
        Specification<Conductor> conductorSpec = EmployeeSpecification.forConductors(nicNumber, name, contactNumber,
                licenseNumber);
        employees.addAll(conductorRepository.findAll(conductorSpec));

        return employees;
    }

    @Override
    public List<Driver> searchDrivers(String nicNumber, String name, String contactNumber, String licenseNumber) {
        Specification<Driver> spec = EmployeeSpecification.forDrivers(nicNumber, name, contactNumber, licenseNumber);
        return driverRepository.findAll(spec);
    }

    @Override
    public List<Conductor> searchConductors(String nicNumber, String name, String contactNumber, String licenseNumber) {
        Specification<Conductor> spec = EmployeeSpecification.forConductors(nicNumber, name, contactNumber,
                licenseNumber);
        return conductorRepository.findAll(spec);
    }
}