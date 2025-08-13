package com.tritonptms.public_transport_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tritonptms.public_transport_management_system.model.Conductor;
import com.tritonptms.public_transport_management_system.service.ConductorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ConductorController.class)
class ConductorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConductorService conductorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/conductors should return all conductors")
    void getAllConductors() throws Exception {
        Conductor conductor = new Conductor();
        conductor.setId(1L);
        conductor.setNicNumber("851234567V");
        when(conductorService.getAllConductors()).thenReturn(List.of(conductor));

        mockMvc.perform(get("/api/conductors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nicNumber").value("851234567V"));
    }

    @Test
    @DisplayName("GET /api/conductors/{id} should return a conductor by ID")
    void getConductorById() throws Exception {
        Conductor conductor = new Conductor();
        conductor.setId(1L);
        when(conductorService.getConductorById(1L)).thenReturn(Optional.of(conductor));

        mockMvc.perform(get("/api/conductors/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("POST /api/conductors should create a new conductor")
    void createConductor() throws Exception {
        Conductor newConductor = new Conductor();
        newConductor.setNicNumber("851234567V");
        newConductor.setFirstName("Samal");
        newConductor.setLastName("Fernando");
        newConductor.setDateOfBirth(new Date());
        newConductor.setContactNumber("0779876543");
        newConductor.setEmail("samal.f@example.com");
        newConductor.setAddress("456 Galle Road, Kalutara");
        newConductor.setDateJoined(new Date());
        newConductor.setIsCurrentEmployee(true);
        newConductor.setConductorLicenseNumber("C987654321");
        newConductor.setLicenseExpirationDate(new Date());
        newConductor.setAvailable(true);

        Conductor savedConductor = new Conductor();
        savedConductor.setId(2L);
        savedConductor.setFirstName("Samal"); // Set the expected value here

        when(conductorService.createConductor(any(Conductor.class))).thenReturn(savedConductor);

        mockMvc.perform(post("/api/conductors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newConductor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Samal"));
    }
    
    @Test
    @DisplayName("PUT /api/conductors/{id} should update an existing conductor")
    void updateConductor() throws Exception {
        Conductor updatedDetails = new Conductor();
        updatedDetails.setNicNumber("851234567V");
        updatedDetails.setFirstName("Ajith");
        updatedDetails.setLastName("Fernando");
        updatedDetails.setDateOfBirth(new Date());
        updatedDetails.setContactNumber("0779876543");
        updatedDetails.setEmail("samal.f@example.com");
        updatedDetails.setAddress("456 Galle Road, Kalutara");
        updatedDetails.setDateJoined(new Date());
        updatedDetails.setIsCurrentEmployee(true);
        updatedDetails.setConductorLicenseNumber("C112233445");
        updatedDetails.setLicenseExpirationDate(new Date());
        updatedDetails.setAvailable(true);
        
        // This is the object returned by the service mock
        Conductor updatedMockReturn = new Conductor();
        updatedMockReturn.setFirstName("Ajith");

        when(conductorService.updateConductor(any(Long.class), any(Conductor.class))).thenReturn(updatedMockReturn);

        mockMvc.perform(put("/api/conductors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ajith"));
    }
    
    @Test
    @DisplayName("DELETE /api/conductors/{id} should delete a conductor")
    void deleteConductor() throws Exception {
        doNothing().when(conductorService).deleteConductor(1L);

        mockMvc.perform(delete("/api/conductors/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}