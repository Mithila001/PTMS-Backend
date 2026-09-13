package com.tritonptms.ptms.feature.route;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long>, JpaSpecificationExecutor<Route> {
    // Additional query methods can be defined here if needed
    // For example, find by route number
    Optional<Route> findByRouteNumber(String routeNumber);
}
