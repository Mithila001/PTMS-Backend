package com.tritonptms.public_transport_management_system.service.specification;

import com.tritonptms.public_transport_management_system.model.Route;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class RouteSpecification {

    public static Specification<Route> hasRouteNumber(String routeNumber) {
        if (!StringUtils.hasText(routeNumber)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("routeNumber")),
                "%" + routeNumber.toLowerCase() + "%");
    }

    public static Specification<Route> hasOrigin(String origin) {
        if (!StringUtils.hasText(origin)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("origin")),
                "%" + origin.toLowerCase() + "%");
    }

    public static Specification<Route> hasDestination(String destination) {
        if (!StringUtils.hasText(destination)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("destination")),
                "%" + destination.toLowerCase() + "%");
    }
}