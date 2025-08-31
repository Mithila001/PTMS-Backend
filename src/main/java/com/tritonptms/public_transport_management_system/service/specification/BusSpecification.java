package com.tritonptms.public_transport_management_system.service.specification;

import com.tritonptms.public_transport_management_system.model.Bus;
import com.tritonptms.public_transport_management_system.model.Bus.ServiceType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class BusSpecification {

    public static Specification<Bus> hasRegistrationNumber(String registrationNumber) {
        if (!StringUtils.hasText(registrationNumber)) {
            return null; // Return null if the search term is empty or null.
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("registrationNumber")),
                "%" + registrationNumber.toLowerCase() + "%");
    }

    public static Specification<Bus> hasServiceType(ServiceType serviceType) {
        if (serviceType == null) {
            return null; // Return null if the search term is empty or null.
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("serviceType"), serviceType);
    }
}