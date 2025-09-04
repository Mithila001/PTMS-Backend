package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.dto.RouteDto;
import com.tritonptms.public_transport_management_system.model.Route;

import java.util.List;
import java.util.Optional;

public interface RouteService {

    List<RouteDto> getAllRoutes();

    Optional<RouteDto> getRouteById(Long id);

    Optional<RouteDto> getRouteByNumber(String routeNumber);

    Route createRoute(RouteDto routeDto);

    Route updateRoute(Long id, RouteDto routeDto);

    void deleteRoute(Long id);

    RouteDto convertToDto(Route route);

    List<Route> searchRoutes(String routeNumber, String origin, String destination);

}