package com.tritonptms.public_transport_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tritonptms.public_transport_management_system.model.Bus;
import com.tritonptms.public_transport_management_system.repository.BusRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.tritonptms.public_transport_management_system.model.Vehicle.FuelType;

@SpringBootTest
@AutoConfigureMockMvc
public class BusControllerIntegrationTest {

     @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusRepository busRepository;

    @AfterEach
    void tearDown() {
        busRepository.deleteAll(); // Clean up the database after each test
    }

    @Test
    void whenPostBus_thenStatus201AndBusIsReturned() throws Exception {
        Bus bus = new Bus();
        bus.setRegistrationNumber("NB-9999");
        bus.setMake("Ashok Leyland");
        bus.setModel("Viking"); // Corrected: Add the missing model field
        bus.setYearOfManufacture(2022); // Corrected: Add the missing yearOfManufacture field
        bus.setFuelType(Bus.FuelType.DIESEL);
        bus.setActive(true); // Corrected: Add the missing isActive field
        bus.setSeatingCapacity(50);
        bus.setStandingCapacity(15);
        bus.setBusType(Bus.BusType.NORMAL);
        bus.setNtcPermitNumber(1234567890);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/buses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bus)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registrationNumber").value("NB-9999"));
    }

    @Test
    void whenGetBus_thenStatus200AndBusIsReturned() throws Exception {
        // First, save a bus to the test database
        Bus bus = new Bus();
        bus.setRegistrationNumber("NB-1111");
        bus.setMake("Ashok Leyland");
        bus.setModel("Viking");
        bus.setYearOfManufacture(2022);
        bus.setFuelType(Bus.FuelType.DIESEL);
        bus.setActive(true);
        bus.setBusType(Bus.BusType.NORMAL);
        bus.setSeatingCapacity(50);
        bus.setStandingCapacity(15);
        bus.setNtcPermitNumber(1234567890);
        busRepository.save(bus);

        // Then, retrieve it via the API endpoint
        mockMvc.perform(MockMvcRequestBuilders.get("/api/buses/{id}", bus.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrationNumber").value("NB-1111"));
    }

    @Test
    void whenPostInvalidBus_thenStatus400AndErrorMessagesAreReturned() throws Exception {
        Bus bus = new Bus();
        bus.setRegistrationNumber("NB-9999");
        bus.setMake("Ashok Leyland");
        bus.setModel("Viking");
        bus.setYearOfManufacture(2022);
        bus.setFuelType(Bus.FuelType.DIESEL);
        bus.setActive(true);
        bus.setSeatingCapacity(0); // This is an invalid value
        bus.setStandingCapacity(-5); // This is another invalid value
        bus.setBusType(null); // This is also an invalid value
        bus.setNtcPermitNumber(123); // Invalid NTC permit number

        mockMvc.perform(MockMvcRequestBuilders.post("/api/buses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bus)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.seatingCapacity").value("Seating capacity must be at least 1"))
                .andExpect(jsonPath("$.errors.standingCapacity").value("Standing capacity must be at least 0"))
                .andExpect(jsonPath("$.errors.busType").value("Bus type is mandatory"))
                .andExpect(jsonPath("$.errors.ntcPermitNumber").value("NTC permit number must be at least 10 digits"));
    }
    
}
