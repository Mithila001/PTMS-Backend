package com.tritonptms.public_transport_management_system.controller;

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
    public ResponseEntity<List<Route>> getAllRoutes() {
        List<Route> routes = routeService.getAllRoutes();
        return new ResponseEntity<>(routes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        Route route = routeService.getRouteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        return new ResponseEntity<>(route, HttpStatus.OK);
    }

    @GetMapping("/number/{routeNumber}")
    public ResponseEntity<Route> getRouteByNumber(@PathVariable String routeNumber) {
        Route route = routeService.getRouteByNumber(routeNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with number: " + routeNumber));
        return new ResponseEntity<>(route, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Route> createRoute(@Valid @RequestBody Route route) {
        Route newRoute = routeService.createRoute(route);
        return new ResponseEntity<>(newRoute, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable Long id, @Valid @RequestBody Route routeDetails) {
        Route updatedRoute = routeService.updateRoute(id, routeDetails);
        return new ResponseEntity<>(updatedRoute, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}