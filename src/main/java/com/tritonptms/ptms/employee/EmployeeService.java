package com.tritonptms.ptms.employee;

import com.tritonptms.ptms.employee.dto.EmployeeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final DriverRepository driverRepository;
    private final ConductorRepository conductorRepository;

    public EmployeeService(DriverRepository driverRepository, ConductorRepository conductorRepository) {
        this.driverRepository = driverRepository;
        this.conductorRepository = conductorRepository;
    }
    public Page<EmployeeDto> searchDrivers(String nicNumber, String name, String contactNumber, String licenseNumber,
            Pageable pageable) {
        Specification<Driver> spec = EmployeeSpecification.forDrivers(nicNumber, name, contactNumber, licenseNumber);
        Page<Driver> driversPage = driverRepository.findAll(spec, pageable);
        return driversPage.map(EmployeeDto::fromEntity);
    }
    public Page<EmployeeDto> searchConductors(String nicNumber, String name, String contactNumber, String licenseNumber,
            Pageable pageable) {
        Specification<Conductor> spec = EmployeeSpecification.forConductors(nicNumber, name, contactNumber,
                licenseNumber);
        Page<Conductor> conductorsPage = conductorRepository.findAll(spec, pageable);
        return conductorsPage.map(EmployeeDto::fromEntity);
    }
}
