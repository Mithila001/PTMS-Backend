package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.dto.RouteDto;
import com.tritonptms.public_transport_management_system.model.Route;
import com.tritonptms.public_transport_management_system.repository.RouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

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
    private RouteDto routeDto;
    private LineString mockLineString;

    @BeforeEach
    void setUp() throws ParseException {
        WKTReader wktReader = new WKTReader();
        mockLineString = (LineString) wktReader.read("LINESTRING(80.000 6.000, 80.001 6.001)");

        route = new Route();
        route.setId(1L);
        route.setRouteNumber("177");
        route.setOrigin("Kaduwela");
        route.setDestination("Kollupitiya");
        route.setMajorStops(List.of("Battaramulla", "Rajagiriya", "Borella"));
        route.setRoutePath(mockLineString);

        routeDto = new RouteDto();
        routeDto.setRouteNumber("177");
        routeDto.setOrigin("Kaduwela");
        routeDto.setDestination("Kollupitiya");
        routeDto.setMajorStops(List.of("Battaramulla", "Rajagiriya", "Borella"));
        routeDto.setRoutePath("LINESTRING(80.000 6.000, 80.001 6.001)");
    }

    private RouteDto convertToDto(Route route) {
        RouteDto dto = new RouteDto();
        dto.setRouteNumber(route.getRouteNumber());
        dto.setOrigin(route.getOrigin());
        dto.setDestination(route.getDestination());
        dto.setMajorStops(route.getMajorStops());
        if (route.getRoutePath() != null) {
            dto.setRoutePath(route.getRoutePath().toText());
        }
        return dto;
    }

    @Test
    @DisplayName("Should return all routes as DTOs")
    void getAllRoutes() {
        when(routeRepository.findAll()).thenReturn(List.of(route));
        List<RouteDto> routes = routeService.getAllRoutes();
        assertNotNull(routes);
        assertEquals(1, routes.size());
        assertEquals(routeDto.getRouteNumber(), routes.get(0).getRouteNumber());
        verify(routeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return a route DTO by its ID")
    void getRouteById() {
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        Optional<RouteDto> foundRoute = routeService.getRouteById(1L);
        assertTrue(foundRoute.isPresent());
        assertEquals(routeDto.getRouteNumber(), foundRoute.get().getRouteNumber());
    }

    @Test
    @DisplayName("Should return a route DTO by its number")
    void getRouteByNumber() {
        when(routeRepository.findByRouteNumber("177")).thenReturn(Optional.of(route));
        Optional<RouteDto> foundRoute = routeService.getRouteByNumber("177");
        assertTrue(foundRoute.isPresent());
        assertEquals(routeDto.getRouteNumber(), foundRoute.get().getRouteNumber());
    }

    @Test
    @DisplayName("Should create a new route from a DTO and save an entity")
    void createRoute() throws ParseException {
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        Route savedRoute = routeService.createRoute(routeDto);
        assertNotNull(savedRoute);
        assertEquals("177", savedRoute.getRouteNumber());
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    @Test
    @DisplayName("Should update an existing route from a DTO")
    void updateRoute() throws ParseException {
        RouteDto updatedRouteDto = new RouteDto();
        updatedRouteDto.setRouteNumber("177 A/C");
        updatedRouteDto.setOrigin("Kaduwela");
        updatedRouteDto.setDestination("Kollupitiya");
        updatedRouteDto.setMajorStops(List.of("Battaramulla", "Rajagiriya"));
        updatedRouteDto.setRoutePath("LINESTRING(80.000 6.000, 80.001 6.001)");

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(routeRepository.save(any(Route.class))).thenReturn(route);

        Route updated = routeService.updateRoute(1L, updatedRouteDto);

        assertNotNull(updated);
        assertEquals("177 A/C", updated.getRouteNumber());
        assertEquals(2, updated.getMajorStops().size());
        verify(routeRepository, times(1)).findById(1L);
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    @Test
    @DisplayName("Should delete a route by its ID")
    void deleteRoute() {
        doNothing().when(routeRepository).deleteById(1L);

        routeService.deleteRoute(1L);

        verify(routeRepository, times(1)).deleteById(1L);
    }
}