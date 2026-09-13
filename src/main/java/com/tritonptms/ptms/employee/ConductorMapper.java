package com.tritonptms.ptms.employee;

import com.tritonptms.ptms.employee.dto.ConductorRequest;
import com.tritonptms.ptms.employee.dto.ConductorResponse;
import org.springframework.stereotype.Component;

@Component
public class ConductorMapper {

    private final EmployeeMapper employeeMapper;

    public ConductorMapper(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public Conductor fromRequest(ConductorRequest request) {
        Conductor conductor = new Conductor();
        apply(conductor, request);
        return conductor;
    }

    public void apply(Conductor conductor, ConductorRequest request) {
        employeeMapper.applyCommon(conductor,
                request.nicNumber(), request.firstName(), request.lastName(), request.dateOfBirth(),
                request.contactNumber(), request.email(), request.address(), request.dateJoined(),
                request.currentEmployee());
        conductor.setConductorLicenseNumber(request.conductorLicenseNumber().trim());
        conductor.setLicenseExpirationDate(employeeMapper.toDate(request.licenseExpirationDate()));
        conductor.setAvailable(request.available());
    }

    public ConductorResponse toResponse(Conductor conductor) {
        return new ConductorResponse(
                conductor.getId(),
                conductor.getNicNumber(),
                conductor.getFirstName(),
                conductor.getLastName(),
                employeeMapper.toLocalDate(conductor.getDateOfBirth()),
                conductor.getContactNumber(),
                conductor.getEmail(),
                conductor.getAddress(),
                employeeMapper.toLocalDate(conductor.getDateJoined()),
                conductor.getIsCurrentEmployee(),
                conductor.getConductorLicenseNumber(),
                employeeMapper.toLocalDate(conductor.getLicenseExpirationDate()),
                conductor.isAvailable());
    }
}
