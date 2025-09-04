package com.tritonptms.public_transport_management_system.controller;

import com.tritonptms.public_transport_management_system.dto.RouteDto;
import com.tritonptms.public_transport_management_system.model.Route;
import com.tritonptms.public_transport_management_system.service.RouteService;
import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    // Use constructor injection
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ResponseEntity<List<RouteDto>> getAllRoutes() {
        List<RouteDto> routes = routeService.getAllRoutes();
        return new ResponseEntity<>(routes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteDto> getRouteById(@PathVariable Long id) {
        RouteDto routeDto = routeService.getRouteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        return new ResponseEntity<>(routeDto, HttpStatus.OK);
    }

    @GetMapping("/number/{routeNumber}")
    public ResponseEntity<RouteDto> getRouteByNumber(@PathVariable String routeNumber) {
        RouteDto routeDto = routeService.getRouteByNumber(routeNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with number: " + routeNumber));
        return new ResponseEntity<>(routeDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RouteDto> createRoute(@Valid @RequestBody RouteDto routeDto) {
        Route newRoute = routeService.createRoute(routeDto);
        RouteDto newRouteDto = new RouteDto();
        newRouteDto.setRouteNumber(newRoute.getRouteNumber());
        newRouteDto.setOrigin(newRoute.getOrigin());
        newRouteDto.setDestination(newRoute.getDestination());
        newRouteDto.setMajorStops(newRoute.getMajorStops());
        return new ResponseEntity<>(newRouteDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteDto> updateRoute(@PathVariable Long id, @Valid @RequestBody RouteDto routeDto) {
        Route updatedRoute = routeService.updateRoute(id, routeDto);
        RouteDto updatedRouteDto = new RouteDto();
        updatedRouteDto.setRouteNumber(updatedRoute.getRouteNumber());
        updatedRouteDto.setOrigin(updatedRoute.getOrigin());
        updatedRouteDto.setDestination(updatedRoute.getDestination());
        updatedRouteDto.setMajorStops(updatedRoute.getMajorStops());
        return new ResponseEntity<>(updatedRouteDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Route>> searchRoutes(
            @RequestParam(required = false) String routeNumber,
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination) {

        List<Route> routes = routeService.searchRoutes(routeNumber, origin, destination);
        return new ResponseEntity<>(routes, HttpStatus.OK);
    }
}