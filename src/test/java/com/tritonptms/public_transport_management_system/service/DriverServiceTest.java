package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import com.tritonptms.public_transport_management_system.model.Driver;
import com.tritonptms.public_transport_management_system.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Integrates Mockito with JUnit 5 for dependency mocking
@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    // Mocking the database dependency
    @Mock
    private DriverRepository driverRepository;

    // Injecting the mocked dependencies into the service implementation
    @InjectMocks
    private DriverServiceImpl driverService;

    private Driver driver;

    // Helper method to create a reusable Driver object for tests
    private Driver createTestDriver(Long id, String nic, String firstName, String lastName) {
        Driver d = new Driver();
        d.setId(id);
        d.setNicNumber(nic);
        d.setFirstName(firstName);
        d.setLastName(lastName);
        d.setDateOfBirth(new Date(946684800000L)); // Jan 1, 2000
        d.setContactNumber("0771234567");
        d.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@ptms.com");
        d.setAddress("Kandy");
        d.setDateJoined(new Date());
        d.setIsCurrentEmployee(true);
        d.setDrivingLicenseNumber("DL-987654");
        d.setLicenseExpirationDate(new Date(1893456000000L)); // Jan 1, 2030
        d.setLicenseClass("Heavy Vehicle");
        d.setNtcLicenseNumber("NTC001");
        d.setNtcLicenseExpirationDate(new Date(1735689600000L)); // Jan 1, 2025
        d.setAvailable(true);
        return d;
    }

    // Set up a clean driver object before each test
    @BeforeEach
    void setUp() {
        driver = createTestDriver(1L, "958765432V", "Nimal", "Silva");
    }

    // -------------------------------------------------------------------------
    // R - Retrieve Tests
    // -------------------------------------------------------------------------

    @Test
    void getAllDrivers_shouldReturnListOfDrivers() {
        // Arrange: Prepare a list of drivers and mock the repository response
        Driver driver2 = createTestDriver(2L, "857654321V", "Kamal", "Rathnayaka");
        List<Driver> driverList = Arrays.asList(driver, driver2);
        when(driverRepository.findAll()).thenReturn(driverList);

        // Act
        List<Driver> result = driverService.getAllDrivers();

        // Assert: Verify results and repository interaction
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(driverRepository, times(1)).findAll();
    }

    @Test
    void getDriverById_shouldReturnDriver_whenFound() {
        // Arrange: Mock successful retrieval by ID
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));

        // Act
        Optional<Driver> result = driverService.getDriverById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Nimal", result.get().getFirstName());
        verify(driverRepository, times(1)).findById(1L);
    }

    @Test
    void getDriverById_shouldReturnEmptyOptional_whenNotFound() {
        // Arrange: Mock failed retrieval by ID
        when(driverRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        Optional<Driver> result = driverService.getDriverById(2L);

        // Assert
        assertFalse(result.isPresent());
        verify(driverRepository, times(1)).findById(2L);
    }

    @Test
    void getDriverByNic_shouldReturnDriver_whenFound() {
        // Arrange: Mock successful retrieval by NIC
        when(driverRepository.findByNicNumber("958765432V")).thenReturn(Optional.of(driver));

        // Act
        Optional<Driver> result = driverService.getDriverByNic("958765432V");

        // Assert
        assertTrue(result.isPresent());
        verify(driverRepository, times(1)).findByNicNumber("958765432V");
    }

    // -------------------------------------------------------------------------
    // C - Create Test
    // -------------------------------------------------------------------------

    @Test
    void createDriver_shouldReturnSavedDriver() {
        // Arrange: Mock the save operation to return the input object
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        // Act
        Driver savedDriver = driverService.createDriver(driver);

        // Assert
        assertNotNull(savedDriver);
        assertEquals("Nimal", savedDriver.getFirstName());
        verify(driverRepository, times(1)).save(driver);
    }

    // -------------------------------------------------------------------------
    // U - Update Tests
    // -------------------------------------------------------------------------

    @Test
    void updateDriver_shouldReturnUpdatedDriver_whenFound() {
        // Arrange: Prepare update details
        Driver updatedDetails = createTestDriver(1L, "958765432V", "UpdatedName", "UpdatedLast");
        updatedDetails.setDrivingLicenseNumber("C9876543");
        updatedDetails.setAvailable(false);

        // Mock the findById to return the original object, and save to return the
        // modified object
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        // Ensures the save method returns the updated object passed to it
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        // Act
        Driver updatedDriver = driverService.updateDriver(1L, updatedDetails);

        // Assert: Check that specific fields (common and driver-specific) were updated
        assertNotNull(updatedDriver);
        assertEquals("UpdatedName", updatedDriver.getFirstName());
        assertEquals("C9876543", updatedDriver.getDrivingLicenseNumber());
        assertFalse(updatedDriver.isAvailable());

        // Verify the interaction sequence
        verify(driverRepository, times(1)).findById(1L);
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void updateDriver_shouldThrowException_whenNotFound() {
        // Arrange: Mock findById to return empty, simulating resource not found
        when(driverRepository.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert: Check that ResourceNotFoundException is thrown
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            driverService.updateDriver(10L, driver);
        });

        String expectedMessage = "Driver not found with id: 10";
        assertTrue(exception.getMessage().contains(expectedMessage));

        // Verify findById was called, and save was NOT called
        verify(driverRepository, times(1)).findById(10L);
        verify(driverRepository, never()).save(any(Driver.class));
    }

    // -------------------------------------------------------------------------
    // D - Delete Test
    // -------------------------------------------------------------------------

    @Test
    void deleteDriver_shouldCallDeleteById() {
        // Act
        driverService.deleteDriver(1L);

        // Assert: Verify that the deleteById method was called
        verify(driverRepository, times(1)).deleteById(1L);
    }
}