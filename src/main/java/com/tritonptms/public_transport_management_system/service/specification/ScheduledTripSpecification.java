package com.tritonptms.public_transport_management_system.service.specification;

import com.tritonptms.public_transport_management_system.model.ScheduledTrip;
import com.tritonptms.public_transport_management_system.model.Route;
import com.tritonptms.public_transport_management_system.model.enums.route.Direction;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import java.time.LocalTime;

public class ScheduledTripSpecification {

    public static Specification<ScheduledTrip> hasScheduledTripId(Long scheduledTripId) {
        // Return null if the search term is null, so it is ignored.
        if (scheduledTripId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), scheduledTripId);
    }

    public static Specification<ScheduledTrip> hasDirection(Direction direction) {
        // Return null if the search term is null, so it is ignored.
        if (direction == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("direction"), direction);
    }

    public static Specification<ScheduledTrip> hasRouteNumber(String routeNumber) {
        // Return null if the search term is empty or null.
        if (!StringUtils.hasText(routeNumber)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            // Join the ScheduledTrip entity with the Route entity.
            Join<ScheduledTrip, Route> routeJoin = root.join("route");
            // Use criteriaBuilder.like to perform a case-insensitive search on the
            // routeNumber.
            return criteriaBuilder.like(
                    criteriaBuilder.lower(routeJoin.get("routeNumber")),
                    "%" + routeNumber.toLowerCase() + "%");
        };
    }
}