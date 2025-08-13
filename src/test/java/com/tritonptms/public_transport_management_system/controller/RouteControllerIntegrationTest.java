package com.tritonptms.public_transport_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tritonptms.public_transport_management_system.model.Route;
import com.tritonptms.public_transport_management_system.service.RouteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean; 

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(RouteController.class)
class RouteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean 
    private RouteService routeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/routes should return all routes")
    void getAllRoutes() throws Exception {
        Route route = new Route();
        route.setId(1L);
        route.setRouteNumber("177");

        when(routeService.getAllRoutes()).thenReturn(List.of(route));

        mockMvc.perform(get("/api/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].routeNumber").value("177"));
    }

    @Test
    @DisplayName("POST /api/routes should create a new route")
    void createRoute() throws Exception {
        Route newRoute = new Route();
        newRoute.setRouteNumber("101");
        newRoute.setOrigin("Piliyandala");
        newRoute.setDestination("Colombo Fort");

        Route savedRoute = new Route();
        savedRoute.setId(2L);
        savedRoute.setRouteNumber("101");
        savedRoute.setOrigin("Piliyandala");
        savedRoute.setDestination("Colombo Fort");

        when(routeService.createRoute(any(Route.class))).thenReturn(savedRoute);

        mockMvc.perform(post("/api/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRoute)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.routeNumber").value("101"));
    }
}