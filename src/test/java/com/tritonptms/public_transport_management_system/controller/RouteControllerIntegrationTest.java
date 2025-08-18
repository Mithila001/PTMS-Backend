package com.tritonptms.public_transport_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tritonptms.public_transport_management_system.dto.RouteDto;
import com.tritonptms.public_transport_management_system.model.Route;
import com.tritonptms.public_transport_management_system.service.RouteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(RouteController.class)
class RouteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RouteService routeService;

    @Autowired
    private ObjectMapper objectMapper;

    // Helper method to create a valid RouteDto
    private RouteDto createValidRouteDto() {
        RouteDto routeDto = new RouteDto();
        routeDto.setRouteNumber("101");
        routeDto.setOrigin("Piliyandala");
        routeDto.setDestination("Colombo Fort");
        routeDto.setRoutePath("LINESTRING(80.000 6.000, 80.001 6.001)");
        return routeDto;
    }

    @Test
    @DisplayName("GET /api/routes should return all routes as DTOs")
    void getAllRoutes() throws Exception {
        RouteDto routeDto = createValidRouteDto();
        when(routeService.getAllRoutes()).thenReturn(List.of(routeDto));

        mockMvc.perform(get("/api/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].routeNumber").value("101"));
    }

    @Test
    @DisplayName("GET /api/routes/{id} should return a route DTO by ID")
    void getRouteById() throws Exception {
        RouteDto routeDto = createValidRouteDto();
        when(routeService.getRouteById(anyLong())).thenReturn(Optional.of(routeDto));

        mockMvc.perform(get("/api/routes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routeNumber").value("101"));
    }

    @Test
    @DisplayName("GET /api/routes/number/{routeNumber} should return a route DTO by number")
    void getRouteByNumber() throws Exception {
        RouteDto routeDto = createValidRouteDto();
        when(routeService.getRouteByNumber(anyString())).thenReturn(Optional.of(routeDto));

        mockMvc.perform(get("/api/routes/number/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routeNumber").value("101"));
    }

    @Test
    @DisplayName("POST /api/routes should create a new route and return its DTO")
    void createRoute() throws Exception {
        RouteDto newRouteDto = createValidRouteDto();

        // Create a mock Route object and populate it with the expected data
        Route savedRoute = new Route();
        savedRoute.setId(2L);
        savedRoute.setRouteNumber(newRouteDto.getRouteNumber());
        savedRoute.setOrigin(newRouteDto.getOrigin());
        savedRoute.setDestination(newRouteDto.getDestination());
        savedRoute.setMajorStops(newRouteDto.getMajorStops());

        // Mock the service to return the populated object
        when(routeService.createRoute(any(RouteDto.class))).thenReturn(savedRoute);

        mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRouteDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.routeNumber").value("101"));
    }
}