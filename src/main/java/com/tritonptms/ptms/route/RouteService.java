package com.tritonptms.ptms.route;

import com.tritonptms.ptms.route.dto.RouteDto;
import com.tritonptms.ptms.common.exception.ResourceNotFoundException;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RouteService {

    private final RouteRepository routeRepository;
    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }
    public RouteDto convertToDto(Route route) {
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
        // route.setId(routeDto.getId());
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
    public List<RouteDto> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    public Optional<RouteDto> getRouteById(Long id) {
        return routeRepository.findById(id)
                .map(this::convertToDto);
    }
    public Optional<RouteDto> getRouteByNumber(String routeNumber) {
        return routeRepository.findByRouteNumber(routeNumber)
                .map(this::convertToDto);
    }
    @Transactional
    public Route createRoute(RouteDto routeDto) {
        Route route = convertToEntity(routeDto);
        Route savedRoute = routeRepository.save(route);

        return savedRoute;
    }
    @Transactional
    public Route updateRoute(Long id, RouteDto routeDto) {
        Route existingRoute = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));

        // Eagerly initialize the majorStops collection to prevent
        // LazyInitializationException
        existingRoute.getMajorStops().size(); // Forces the collection to load

        // Create a copy of the existing route before it is updated
        Route oldRoute = new Route();
        oldRoute.setId(existingRoute.getId());
        oldRoute.setRouteNumber(existingRoute.getRouteNumber());
        oldRoute.setOrigin(existingRoute.getOrigin());
        oldRoute.setDestination(existingRoute.getDestination());
        oldRoute.setMajorStops(new ArrayList<>(existingRoute.getMajorStops())); // Correctly copy the initialized list
        oldRoute.setRoutePath(existingRoute.getRoutePath());

        // Update the existing route with new data
        Route updatedRoute = convertToEntity(routeDto);
        existingRoute.setRouteNumber(updatedRoute.getRouteNumber());
        existingRoute.setOrigin(updatedRoute.getOrigin());
        existingRoute.setDestination(updatedRoute.getDestination());
        existingRoute.setMajorStops(updatedRoute.getMajorStops());
        existingRoute.setRoutePath(updatedRoute.getRoutePath());

        Route savedRoute = routeRepository.save(existingRoute);

        return savedRoute;
    }
    @Transactional
    public void deleteRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));

        routeRepository.delete(route);

    }
    public Page<RouteDto> searchRoutes(String routeNumber, String origin, String destination, Pageable pageable) {
        Specification<Route> combinedSpec = Specification.allOf(
                RouteSpecification.hasRouteNumber(routeNumber),
                RouteSpecification.hasOrigin(origin),
                RouteSpecification.hasDestination(destination));

        Page<Route> routePage = routeRepository.findAll(combinedSpec, pageable);
        return routePage.map(this::convertToDto);
    }
}
