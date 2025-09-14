package com.tritonptms.public_transport_management_system.service.specification;

import com.tritonptms.public_transport_management_system.model.Conductor;
import com.tritonptms.public_transport_management_system.model.Driver;
import com.tritonptms.public_transport_management_system.model.Employee;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecification {

    public static <T extends Employee> Specification<T> hasNicNumber(String nicNumber) {
        if (!StringUtils.hasText(nicNumber)) {
            return Specification.where(null);
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("nicNumber")),
                "%" + nicNumber.toLowerCase() + "%");
    }

    public static <T extends Employee> Specification<T> hasName(String name) {
        if (!StringUtils.hasText(name)) {
            return Specification.where(null);
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
            return Specification.where(null);
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                root.get("contactNumber"),
                "%" + contactNumber + "%");
    }

    public static Specification<Driver> forDrivers(String nicNumber, String name, String contactNumber,
            String licenseNumber) {
        List<Specification<Driver>> specifications = new ArrayList<>();
        specifications.add(hasNicNumber(nicNumber));
        specifications.add(hasName(name));
        specifications.add(hasContactNumber(contactNumber));
        if (StringUtils.hasText(licenseNumber)) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("drivingLicenseNumber")),
                    "%" + licenseNumber.toLowerCase() + "%"));
        }
        return Specification.allOf(specifications);
    }

    public static Specification<Conductor> forConductors(String nicNumber, String name, String contactNumber,
            String licenseNumber) {
        List<Specification<Conductor>> specifications = new ArrayList<>();
        specifications.add(hasNicNumber(nicNumber));
        specifications.add(hasName(name));
        specifications.add(hasContactNumber(contactNumber));
        if (StringUtils.hasText(licenseNumber)) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("conductorLicenseNumber")),
                    "%" + licenseNumber.toLowerCase() + "%"));
        }
        return Specification.allOf(specifications);
    }
}