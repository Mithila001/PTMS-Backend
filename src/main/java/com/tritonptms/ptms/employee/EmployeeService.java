package com.tritonptms.ptms.employee;

import com.tritonptms.ptms.employee.dto.EmployeeSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@PreAuthorize("hasAnyRole('ADMIN','OPERATIONS_MANAGER')")
public class EmployeeService {

    private final DriverRepository driverRepository;
    private final ConductorRepository conductorRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(DriverRepository driverRepository,
            ConductorRepository conductorRepository,
            EmployeeMapper employeeMapper) {
        this.driverRepository = driverRepository;
        this.conductorRepository = conductorRepository;
        this.employeeMapper = employeeMapper;
    }

    public Page<EmployeeSummaryResponse> searchDrivers(String nicNumber,
            String name,
            String contactNumber,
            String licenseNumber,
            Pageable pageable) {
        Specification<Driver> specification = EmployeeSpecification.forDrivers(
                nicNumber, name, contactNumber, licenseNumber);
        return driverRepository.findAll(specification, pageable).map(employeeMapper::toSummary);
    }

    public Page<EmployeeSummaryResponse> searchConductors(String nicNumber,
            String name,
            String contactNumber,
            String licenseNumber,
            Pageable pageable) {
        Specification<Conductor> specification = EmployeeSpecification.forConductors(
                nicNumber, name, contactNumber, licenseNumber);
        return conductorRepository.findAll(specification, pageable).map(employeeMapper::toSummary);
    }
}
