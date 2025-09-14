package com.tritonptms.public_transport_management_system.service.specification;

import com.tritonptms.public_transport_management_system.model.Assignment;
import com.tritonptms.public_transport_management_system.model.enums.assignment.AssignmentStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public class AssignmentSpecification {

    public static Specification<Assignment> hasScheduledTripId(Long scheduledTripId) {
        if (scheduledTripId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("scheduledTrip").get("id"),
                scheduledTripId);
    }

    public static Specification<Assignment> hasBusId(Long busId) {
        if (busId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("bus").get("id"), busId);
    }

    public static Specification<Assignment> hasDriverId(Long driverId) {
        if (driverId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("driver").get("id"), driverId);
    }

    public static Specification<Assignment> hasConductorId(Long conductorId) {
        if (conductorId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("conductor").get("id"), conductorId);
    }

    public static Specification<Assignment> hasDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("date"), date);
    }

    public static Specification<Assignment> hasStatus(AssignmentStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Assignment> driverNameContains(String driverName) {
        if (!StringUtils.hasText(driverName)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("driver").get("firstName")),
                "%" + driverName.toLowerCase() + "%");
    }

    public static Specification<Assignment> conductorNameContains(String conductorName) {
        if (!StringUtils.hasText(conductorName)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("conductor").get("firstName")),
                "%" + conductorName.toLowerCase() + "%");
    }
}