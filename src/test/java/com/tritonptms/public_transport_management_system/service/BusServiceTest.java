package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import com.tritonptms.public_transport_management_system.model.Bus;
import com.tritonptms.public_transport_management_system.model.Bus.ComfortType;
import com.tritonptms.public_transport_management_system.model.Bus.ServiceType;
import com.tritonptms.public_transport_management_system.model.Vehicle.FuelType;
import com.tritonptms.public_transport_management_system.repository.BusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the BusServiceImpl.
 * We use Mockito to isolate the service logic from the repository, ensuring a
 * fast and focused test.
 */
@ExtendWith(MockitoExtension.class)
class BusServiceTest {

    // Mock the dependency (BusRepository)
    @Mock
    private BusRepository busRepository;

    // Inject the mocks into the service class we are testing
    @InjectMocks
    private BusServiceImpl busService;

    // A sample Bus object to be used across tests
    private Bus bus;

    /**
     * Setup method runs before each test. It initializes a common bus object.
     */
    @BeforeEach
    void setUp() {
        bus = new Bus();
        bus.setId(1L);
        bus.setRegistrationNumber("NB-1234");
        bus.setMake("Lanka Ashok Leyland");
        bus.setModel("Viking");
        bus.setYearOfManufacture(2018);
        bus.setFuelType(FuelType.DIESEL);
        bus.setActive(true);
        bus.setSeatingCapacity(50);
        bus.setStandingCapacity(15);
        bus.setNtcPermitNumber(1000000001L);
        bus.setComfortType(ComfortType.NORMAL);
        bus.setIsA_C(false);
        bus.setServiceType(ServiceType.PRIVATE_BUS);
    }

    // --- Test Cases for getAllBuses ---

    @Test
    @DisplayName("Should retrieve all buses successfully")
    void getAllBuses_shouldReturnListOfBuses() {
        // Arrange: Prepare data for the mock repository to return
        Bus bus2 = new Bus();
        bus2.setId(2L);
        List<Bus> busList = Arrays.asList(bus, bus2);

        // Mocking: Define the behavior of the mocked repository
        when(busRepository.findAll()).thenReturn(busList);

        // Act: Call the service method we are testing
        List<Bus> result = busService.getAllBuses();

        // Assert: Verify the result and mock interactions (AAA Pattern)
        assertNotNull(result, "The returned list should not be null");
        assertEquals(2, result.size(), "The returned list size should match the mock data");
        assertEquals("NB-1234", result.get(0).getRegistrationNumber(), "Registration number should match");

        // Verification: Ensure the mock method was called exactly once
        verify(busRepository, times(1)).findAll();
    }

    // --- Test Cases for getBusById ---

    @Test
    @DisplayName("Should retrieve a bus by ID when it exists")
    void getBusById_shouldReturnBus() {
        // Arrange
        Long busId = 1L;
        // Mocking
        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));

        // Act
        Optional<Bus> result = busService.getBusById(busId);

        // Assert
        assertTrue(result.isPresent(), "Bus should be found");
        assertEquals(busId, result.get().getId(), "Returned bus ID should match the requested ID");
        verify(busRepository, times(1)).findById(busId);
    }

    @Test
    @DisplayName("Should return empty optional when bus ID does not exist")
    void getBusById_shouldReturnEmptyOptional() {
        // Arrange
        Long busId = 99L;
        // Mocking
        when(busRepository.findById(busId)).thenReturn(Optional.empty());

        // Act
        Optional<Bus> result = busService.getBusById(busId);

        // Assert
        assertFalse(result.isPresent(), "Bus should not be found, so optional should be empty");
        verify(busRepository, times(1)).findById(busId);
    }

    // --- Test Cases for saveBus ---

    @Test
    @DisplayName("Should save a new bus successfully")
    void saveBus_shouldReturnSavedBus() {
        // Arrange: Mock the save operation to return the bus object itself
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        // Act
        Bus savedBus = busService.saveBus(bus);

        // Assert
        assertNotNull(savedBus, "The saved bus object should not be null");
        assertEquals("NB-1234", savedBus.getRegistrationNumber(), "The registration number should be correct");
        // Verify that busRepository's save method was called once with any Bus object
        verify(busRepository, times(1)).save(bus);
    }

    // --- Test Cases for updateBus ---

    @Test
    @DisplayName("Should update an existing bus successfully")
    void updateBus_shouldReturnUpdatedBus() {
        // Arrange
        Long busId = 1L;
        Bus updatedDetails = new Bus();
        updatedDetails.setRegistrationNumber("NB-9999"); // New value
        updatedDetails.setMake("Volvo");
        updatedDetails.setIsA_C(true); // New value
        // Copy other necessary fields from setup bus to avoid NullPointer in Service
        updatedDetails.setModel(bus.getModel());
        updatedDetails.setYearOfManufacture(bus.getYearOfManufacture());
        updatedDetails.setFuelType(bus.getFuelType());
        updatedDetails.setActive(bus.isActive());
        updatedDetails.setSeatingCapacity(bus.getSeatingCapacity());
        updatedDetails.setStandingCapacity(bus.getStandingCapacity());
        updatedDetails.setNtcPermitNumber(bus.getNtcPermitNumber());
        updatedDetails.setComfortType(bus.getComfortType());
        updatedDetails.setServiceType(bus.getServiceType());

        // Mocking: 1. Find the existing bus 2. Save the updated bus
        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));
        when(busRepository.save(any(Bus.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the
                                                                                                      // argument passed
                                                                                                      // to save

        // Act
        Bus result = busService.updateBus(busId, updatedDetails);

        // Assert
        assertNotNull(result, "The updated bus should not be null");
        assertEquals("NB-9999", result.getRegistrationNumber(), "The registration number should be updated");
        assertTrue(result.getIsA_C(), "The A/C status should be updated");

        // Verification
        verify(busRepository, times(1)).findById(busId);
        verify(busRepository, times(1)).save(bus);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating a non-existent bus")
    void updateBus_shouldThrowExceptionForNotFound() {
        // Arrange
        Long busId = 99L;
        // Mocking
        when(busRepository.findById(busId)).thenReturn(Optional.empty());

        // Act & Assert: Use assertThrows to check for the expected exception
        assertThrows(ResourceNotFoundException.class, () -> {
            busService.updateBus(busId, bus);
        }, "Should throw ResourceNotFoundException for a non-existent bus");

        // Verification: Verify findById was called, but save was NOT.
        verify(busRepository, times(1)).findById(busId);
        verify(busRepository, times(0)).save(any(Bus.class));
    }

    // --- Test Cases for deleteBus ---

    @Test
    @DisplayName("Should call repository's deleteById method")
    void deleteBus_shouldCallDeleteById() {
        // Arrange
        Long busId = 1L;
        // Mockito's 'deleteById' method for void return type does not need 'when',
        // but we can use 'doNothing()' for clarity or 'verify' after 'act'.
        doNothing().when(busRepository).deleteById(busId);

        // Act
        busService.deleteBus(busId);

        // Assert/Verify
        // This is the core check: verifying the repository's method was called once
        verify(busRepository, times(1)).deleteById(busId);
    }

    // NOTE: We are intentionally skipping the test for 'searchBuses' as it involves
    // mocking complex Spring Data features (Specification, Pageable, Page),
    // which is better suited for a dedicated lesson on advanced Mockito/Spring Data
    // testing.
}