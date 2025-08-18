package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.dto.RouteDto;
import com.tritonptms.public_transport_management_system.model.Route;
import com.tritonptms.public_transport_management_system.repository.RouteRepository;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;

    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    private RouteDto convertToDto(Route route) {
        RouteDto routeDto = new RouteDto();
        routeDto.setId(route.getId());
        routeDto.setRouteNumber(route.getRouteNumber());
        routeDto.setOrigin(route.getOrigin());
        routeDto.setDestination(route.getDestination());
        routeDto.setMajorStops(route.getMajorStops());
        if (route.getRoutePath() != null) {
            WKTWriter wktWriter = new WKTWriter();
            routeDto.setRoutePath(wktWriter.write(route.getRoutePath()));
        }
        return routeDto;
    }

    private Route convertToEntity(RouteDto routeDto) {
        Route route = new Route();
        //route.setId(routeDto.getId());
        route.setRouteNumber(routeDto.getRouteNumber());
        route.setOrigin(routeDto.getOrigin());
        route.setDestination(routeDto.getDestination());
        route.setMajorStops(routeDto.getMajorStops());

        if (routeDto.getRoutePath() != null && !routeDto.getRoutePath().isEmpty()) {
            try {
                GeometryFactory geometryFactory = new GeometryFactory();
                WKTReader wktReader = new WKTReader(geometryFactory);
                LineString routePath = (LineString) wktReader.read(routeDto.getRoutePath());
                route.setRoutePath(routePath);
            } catch (ParseException e) {
                throw new RuntimeException("Failed to parse route path: " + e.getMessage(), e);
            }
        }
        return route;
    }

    @Override
    public List<RouteDto> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RouteDto> getRouteById(Long id) {
        return routeRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public Optional<RouteDto> getRouteByNumber(String routeNumber) {
        return routeRepository.findByRouteNumber(routeNumber)
                .map(this::convertToDto);
    }

    @Override
    public Route createRoute(RouteDto routeDto) {
        Route route = convertToEntity(routeDto);
        return routeRepository.save(route);
    }

    @Override
    public Route updateRoute(Long id, RouteDto routeDto) {
        Route existingRoute = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));
        
        Route updatedRoute = convertToEntity(routeDto);
        existingRoute.setRouteNumber(updatedRoute.getRouteNumber());
        existingRoute.setOrigin(updatedRoute.getOrigin());
        existingRoute.setDestination(updatedRoute.getDestination());
        existingRoute.setMajorStops(updatedRoute.getMajorStops());
        existingRoute.setRoutePath(updatedRoute.getRoutePath());

        return routeRepository.save(existingRoute);
    }

    @Override
    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }
}