package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Route;
import com.tritonptms.public_transport_management_system.repository.RouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceImplTest {

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private RouteServiceImpl routeService;

    private Route route;

    @BeforeEach
    void setUp() {
        route = new Route();
        route.setId(1L);
        route.setRouteNumber("177");
        route.setOrigin("Kaduwela");
        route.setDestination("Kollupitiya");
        route.setMajorStops(List.of("Battaramulla", "Rajagiriya", "Borella"));
    }

    @Test
    @DisplayName("Should return all routes")
    void getAllRoutes() {
        // Given
        when(routeRepository.findAll()).thenReturn(List.of(route));

        // When
        List<Route> routes = routeService.getAllRoutes();

        // Then
        assertNotNull(routes);
        assertEquals(1, routes.size());
        verify(routeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return a route by its ID")
    void getRouteById() {
        // Given
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));

        // When
        Optional<Route> foundRoute = routeService.getRouteById(1L);

        // Then
        assertTrue(foundRoute.isPresent());
        assertEquals("177", foundRoute.get().getRouteNumber());
    }

    @Test
    @DisplayName("Should return a route by its number")
    void getRouteByNumber() {
        // Given
        when(routeRepository.findByRouteNumber("177")).thenReturn(Optional.of(route));

        // When
        Optional<Route> foundRoute = routeService.getRouteByNumber("177");

        // Then
        assertTrue(foundRoute.isPresent());
        assertEquals("Kaduwela", foundRoute.get().getOrigin());
    }

    @Test
    @DisplayName("Should create a new route")
    void createRoute() {
        // Given
        when(routeRepository.save(any(Route.class))).thenReturn(route);

        // When
        Route savedRoute = routeService.createRoute(route);

        // Then
        assertNotNull(savedRoute);
        assertEquals("177", savedRoute.getRouteNumber());
        verify(routeRepository, times(1)).save(route);
    }

    @Test
    @DisplayName("Should update an existing route")
    void updateRoute() {
        // Given
        Route updatedRouteDetails = new Route();
        updatedRouteDetails.setRouteNumber("177 A/C");
        updatedRouteDetails.setOrigin("Kaduwela");
        updatedRouteDetails.setDestination("Kollupitiya");
        updatedRouteDetails.setMajorStops(List.of("Battaramulla", "Rajagiriya"));

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(routeRepository.save(any(Route.class))).thenReturn(updatedRouteDetails);

        // When
        Route updated = routeService.updateRoute(1L, updatedRouteDetails);

        // Then
        assertNotNull(updated);
        assertEquals("177 A/C", updated.getRouteNumber());
        assertEquals(2, updated.getMajorStops().size());
        verify(routeRepository, times(1)).findById(1L);
        verify(routeRepository, times(1)).save(route);
    }
    
    @Test
    @DisplayName("Should delete a route by its ID")
    void deleteRoute() {
        // Given
        doNothing().when(routeRepository).deleteById(1L);

        // When
        routeService.deleteRoute(1L);

        // Then
        verify(routeRepository, times(1)).deleteById(1L);
    }
}