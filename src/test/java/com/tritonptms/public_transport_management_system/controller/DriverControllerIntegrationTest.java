// package com.tritonptms.public_transport_management_system.controller;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.tritonptms.public_transport_management_system.model.Driver;
// import
// com.tritonptms.public_transport_management_system.service.DriverService;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;

// import java.util.Collections;
// import java.util.Date;
// import java.util.List;
// import java.util.Optional;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.doNothing;
// import static org.mockito.Mockito.when;
// import static
// org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static
// org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import static
// org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// @WebMvcTest(DriverController.class)
// class DriverControllerIntegrationTest {

// @Autowired
// private MockMvc mockMvc;

// @MockitoBean
// private DriverService driverService;

// @Autowired
// private ObjectMapper objectMapper;

// @Test
// @DisplayName("GET /api/drivers should return all drivers")
// void getAllDrivers() throws Exception {
// Driver driver = new Driver();
// driver.setId(1L);
// driver.setNicNumber("901234567V");
// when(driverService.getAllDrivers()).thenReturn(List.of(driver));

// mockMvc.perform(get("/api/drivers"))
// .andExpect(status().isOk())
// .andExpect(jsonPath("$[0].nicNumber").value("901234567V"));
// }

// @Test
// @DisplayName("GET /api/drivers/{id} should return a driver by ID")
// void getDriverById() throws Exception {
// Driver driver = new Driver();
// driver.setId(1L);
// when(driverService.getDriverById(1L)).thenReturn(Optional.of(driver));

// mockMvc.perform(get("/api/drivers/{id}", 1L))
// .andExpect(status().isOk())
// .andExpect(jsonPath("$.id").value(1L));
// }

// @Test
// @DisplayName("POST /api/drivers should create a new driver")
// void createDriver() throws Exception {
// Driver newDriver = new Driver();
// newDriver.setNicNumber("901234567V");
// newDriver.setFirstName("Kamal");
// newDriver.setLastName("Perera");
// newDriver.setDateOfBirth(new Date());
// newDriver.setContactNumber("0711234567");
// newDriver.setEmail("kamal.p@example.com");
// newDriver.setAddress("123 Main Street, Colombo");
// newDriver.setDateJoined(new Date());
// newDriver.setIsCurrentEmployee(true);
// newDriver.setDrivingLicenseNumber("D123456789");
// newDriver.setLicenseExpirationDate(new Date());
// newDriver.setLicenseClass("Class D");
// newDriver.setAvailable(true);

// Driver savedDriver = new Driver();
// savedDriver.setId(2L);
// savedDriver.setFirstName("Kamal"); // Set the expected value here
// // No need to set all other fields if you're only asserting on firstName.

// when(driverService.createDriver(any(Driver.class))).thenReturn(savedDriver);

// mockMvc.perform(post("/api/drivers")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(newDriver)))
// .andExpect(status().isCreated())
// .andExpect(jsonPath("$.firstName").value("Kamal"));
// }

// @Test
// @DisplayName("PUT /api/drivers/{id} should update an existing driver")
// void updateDriver() throws Exception {
// Driver updatedDetails = new Driver();
// updatedDetails.setNicNumber("901234567V");
// updatedDetails.setFirstName("Sunil");
// updatedDetails.setLastName("Perera");
// updatedDetails.setDateOfBirth(new Date());
// updatedDetails.setContactNumber("0711234567");
// updatedDetails.setEmail("kamal.p@example.com");
// updatedDetails.setAddress("123 Main Street, Colombo");
// updatedDetails.setDateJoined(new Date());
// updatedDetails.setIsCurrentEmployee(true);
// updatedDetails.setDrivingLicenseNumber("D987654321");
// updatedDetails.setLicenseExpirationDate(new Date());
// updatedDetails.setLicenseClass("Class D");
// updatedDetails.setAvailable(true);

// // This is the object returned by the service mock
// Driver updatedMockReturn = new Driver();
// updatedMockReturn.setFirstName("Sunil");

// when(driverService.updateDriver(any(Long.class),
// any(Driver.class))).thenReturn(updatedMockReturn);

// mockMvc.perform(put("/api/drivers/{id}", 1L)
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(updatedDetails)))
// .andExpect(status().isOk())
// .andExpect(jsonPath("$.firstName").value("Sunil"));
// }

// @Test
// @DisplayName("DELETE /api/drivers/{id} should delete a driver")
// void deleteDriver() throws Exception {
// doNothing().when(driverService).deleteDriver(1L);

// mockMvc.perform(delete("/api/drivers/{id}", 1L))
// .andExpect(status().isNoContent());
// }
// }