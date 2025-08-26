package com.tritonptms.public_transport_management_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tritonptms.public_transport_management_system.dto.LogDetail;
import com.tritonptms.public_transport_management_system.dto.RouteDto;
import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import com.tritonptms.public_transport_management_system.model.ActionLog.ActionType;
import com.tritonptms.public_transport_management_system.model.Route;
import com.tritonptms.public_transport_management_system.repository.RouteRepository;
import com.tritonptms.public_transport_management_system.utils.ObjectComparisonUtil;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final ActionLogService actionLogService;
    private final ObjectMapper objectMapper;

    public RouteServiceImpl(RouteRepository routeRepository, ActionLogService actionLogService,
            ObjectMapper objectMapper) {
        this.routeRepository = routeRepository;
        this.actionLogService = actionLogService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
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
        Route savedRoute = routeRepository.save(route);

        // Log the creation action with details
        try {
            List<LogDetail> changes = ObjectComparisonUtil.compareObjects(new Route(), savedRoute);
            String details = objectMapper.writeValueAsString(changes);
            actionLogService.logAction(ActionType.CREATE, "Route", savedRoute.getId(), details);
        } catch (JsonProcessingException e) {
            // Log the error but don't stop the main process
            System.err.println("Error logging route creation: " + e.getMessage());

        }
        return savedRoute;
    }

    @Override
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

        // Log the update action with detailed changes
        try {
            List<LogDetail> changes = ObjectComparisonUtil.compareObjects(oldRoute, savedRoute);
            String details = objectMapper.writeValueAsString(changes);
            actionLogService.logAction(ActionType.UPDATE, "Route", savedRoute.getId(), details);
        } catch (JsonProcessingException e) {
            System.err.println("Error logging route update: " + e.getMessage());
        }

        return savedRoute;
    }

    @Override
    public void deleteRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));

        routeRepository.delete(route);

        // Log the deletion action with details
        try {
            List<LogDetail> changes = ObjectComparisonUtil.compareObjects(route, new Route());
            String details = objectMapper.writeValueAsString(changes);
            actionLogService.logAction(ActionType.DELETE, "Route", route.getId(), details);
        } catch (JsonProcessingException e) {
            System.err.println("Error logging route deletion: " + e.getMessage());
        }
    }
}