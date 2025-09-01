package com.tritonptms.public_transport_management_system.service.specification;

import com.tritonptms.public_transport_management_system.model.Conductor;
import com.tritonptms.public_transport_management_system.model.Driver;
import com.tritonptms.public_transport_management_system.model.Employee;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class EmployeeSpecification {

    // General purpose specifications for common fields
    public static <T extends Employee> Specification<T> hasNicNumber(String nicNumber) {
        if (!StringUtils.hasText(nicNumber)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("nicNumber")),
                "%" + nicNumber.toLowerCase() + "%");
    }

    public static <T extends Employee> Specification<T> hasName(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            String lowerCaseName = name.toLowerCase();
            Path<String> firstNamePath = root.get("firstName");
            Path<String> lastNamePath = root.get("lastName");
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(firstNamePath), "%" + lowerCaseName + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(lastNamePath), "%" + lowerCaseName + "%"));
        };
    }

    public static <T extends Employee> Specification<T> hasContactNumber(String contactNumber) {
        if (!StringUtils.hasText(contactNumber)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                root.get("contactNumber"),
                "%" + contactNumber + "%");
    }

    // Driver-specific specification
    public static Specification<Driver> hasDrivingLicense(String licenseNumber) {
        if (!StringUtils.hasText(licenseNumber)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("drivingLicenseNumber")),
                "%" + licenseNumber.toLowerCase() + "%");
    }

    // Conductor-specific specification
    public static Specification<Conductor> hasConductorLicense(String licenseNumber) {
        if (!StringUtils.hasText(licenseNumber)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("conductorLicenseNumber")),
                "%" + licenseNumber.toLowerCase() + "%");
    }

    // Helper methods to combine specifications for specific employee types
    public static Specification<Driver> forDrivers(String nicNumber, String name, String contactNumber,
            String licenseNumber) {
        return Specification.allOf(
                hasNicNumber(nicNumber),
                hasName(name),
                hasContactNumber(contactNumber),
                hasDrivingLicense(licenseNumber));
    }

    public static Specification<Conductor> forConductors(String nicNumber, String name, String contactNumber,
            String licenseNumber) {
        return Specification.allOf(
                hasNicNumber(nicNumber),
                hasName(name),
                hasContactNumber(contactNumber),
                hasConductorLicense(licenseNumber));
    }
}