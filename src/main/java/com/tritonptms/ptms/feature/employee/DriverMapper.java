package com.tritonptms.ptms.feature.employee;

import com.tritonptms.ptms.feature.employee.dto.DriverRequest;
import com.tritonptms.ptms.feature.employee.dto.DriverResponse;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    private final EmployeeMapper employeeMapper;

    public DriverMapper(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public Driver fromRequest(DriverRequest request) {
        Driver driver = new Driver();
        apply(driver, request);
        return driver;
    }

    public void apply(Driver driver, DriverRequest request) {
        employeeMapper.applyCommon(driver,
                request.nicNumber(), request.firstName(), request.lastName(), request.dateOfBirth(),
                request.contactNumber(), request.email(), request.address(), request.dateJoined(),
                request.currentEmployee());
        driver.setDrivingLicenseNumber(request.drivingLicenseNumber().trim());
        driver.setLicenseExpirationDate(employeeMapper.toDate(request.licenseExpirationDate()));
        driver.setLicenseClass(request.licenseClass().trim());
        driver.setNtcLicenseNumber(trimToNull(request.ntcLicenseNumber()));
        driver.setNtcLicenseExpirationDate(employeeMapper.toDate(request.ntcLicenseExpirationDate()));
        driver.setAvailable(request.available());
    }

    public DriverResponse toResponse(Driver driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getNicNumber(),
                driver.getFirstName(),
                driver.getLastName(),
                employeeMapper.toLocalDate(driver.getDateOfBirth()),
                driver.getContactNumber(),
                driver.getEmail(),
                driver.getAddress(),
                employeeMapper.toLocalDate(driver.getDateJoined()),
                driver.getIsCurrentEmployee(),
                driver.getDrivingLicenseNumber(),
                employeeMapper.toLocalDate(driver.getLicenseExpirationDate()),
                driver.getLicenseClass(),
                driver.getNtcLicenseNumber(),
                employeeMapper.toLocalDate(driver.getNtcLicenseExpirationDate()),
                driver.isAvailable());
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
