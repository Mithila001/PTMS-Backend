package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Route;

import java.util.List;
import java.util.Optional;

public interface RouteService {

    List<Route> getAllRoutes();

    Optional<Route> getRouteById(Long id);

    Optional<Route> getRouteByNumber(String routeNumber);

    Route createRoute(Route route);

    Route updateRoute(Long id, Route routeDetails);

    void deleteRoute(Long id);

}
