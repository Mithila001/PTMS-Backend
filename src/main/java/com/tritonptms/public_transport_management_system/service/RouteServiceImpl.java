package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Route;
import com.tritonptms.public_transport_management_system.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;

    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    @Override
    public Optional<Route> getRouteById(Long id) {
        return routeRepository.findById(id);
    }

    @Override
    public Optional<Route> getRouteByNumber(String routeNumber) {
        return routeRepository.findByRouteNumber(routeNumber);
    }

    @Override
    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }

    @Override
    public Route updateRoute(Long id, Route routeDetails) {
        Route existingRoute = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id)); // Using a generic exception for now

        existingRoute.setRouteNumber(routeDetails.getRouteNumber());
        existingRoute.setOrigin(routeDetails.getOrigin());
        existingRoute.setDestination(routeDetails.getDestination());
        existingRoute.setMajorStops(routeDetails.getMajorStops());

        return routeRepository.save(existingRoute);
    }

    @Override
    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }
}