package com.tritonptms.ptms.feature.employee;

import com.tritonptms.ptms.feature.employee.dto.EmployeeSummaryResponse;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class EmployeeMapper {

    public void applyCommon(Employee employee,
            String nicNumber,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String contactNumber,
            String email,
            String address,
            LocalDate dateJoined,
            boolean currentEmployee) {
        employee.setNicNumber(nicNumber.trim());
        employee.setFirstName(firstName.trim());
        employee.setLastName(lastName.trim());
        employee.setDateOfBirth(toDate(dateOfBirth));
        employee.setContactNumber(contactNumber.trim());
        employee.setEmail(email.trim().toLowerCase());
        employee.setAddress(address.trim());
        employee.setDateJoined(toDate(dateJoined));
        employee.setIsCurrentEmployee(currentEmployee);
    }

    public EmployeeSummaryResponse toSummary(Employee employee) {
        String type;
        String licenseNumber;
        boolean available;
        if (employee instanceof Driver driver) {
            type = "DRIVER";
            licenseNumber = driver.getDrivingLicenseNumber();
            available = driver.isAvailable();
        } else if (employee instanceof Conductor conductor) {
            type = "CONDUCTOR";
            licenseNumber = conductor.getConductorLicenseNumber();
            available = conductor.isAvailable();
        } else {
            type = "EMPLOYEE";
            licenseNumber = null;
            available = false;
        }

        return new EmployeeSummaryResponse(
                employee.getId(),
                type,
                employee.getNicNumber(),
                employee.getFirstName(),
                employee.getLastName(),
                toLocalDate(employee.getDateOfBirth()),
                employee.getContactNumber(),
                employee.getEmail(),
                employee.getAddress(),
                toLocalDate(employee.getDateJoined()),
                employee.getIsCurrentEmployee(),
                licenseNumber,
                available);
    }

    public LocalDate toLocalDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public java.util.Date toDate(LocalDate date) {
        return date == null ? null : Date.valueOf(date);
    }
}
