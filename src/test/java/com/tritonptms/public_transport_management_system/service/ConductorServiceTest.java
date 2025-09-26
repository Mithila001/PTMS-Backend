package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import com.tritonptms.public_transport_management_system.model.Conductor;
import com.tritonptms.public_transport_management_system.repository.ConductorRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// This annotation integrates Mockito with JUnit 5
@ExtendWith(MockitoExtension.class)
class ConductorServiceTest {

    // Mock the dependency (the database repository)
    @Mock
    private ConductorRepository conductorRepository;

    // Inject the mocks into the service class we are testing
    @InjectMocks
    private ConductorServiceImpl conductorService;

    // Helper Conductor object for testing
    private Conductor conductor;

    // This method runs before each test to set up a clean Conductor object
    @BeforeEach
    void setUp() {
        conductor = createTestConductor(1L, "901234567V", "Amara", "Perera");
    }

    // Helper method to create a Conductor object for testing
    private Conductor createTestConductor(Long id, String nic, String firstName, String lastName) {
        Conductor c = new Conductor();
        c.setId(id);
        c.setNicNumber(nic);
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setDateOfBirth(new Date(946684800000L)); // Jan 1, 2000
        c.setContactNumber("0711234567");
        c.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@ptms.com");
        c.setAddress("Colombo");
        c.setDateJoined(new Date());
        c.setIsCurrentEmployee(true);
        c.setConductorLicenseNumber("L-C-12345");
        c.setLicenseExpirationDate(new Date(1893456000000L)); // Jan 1, 2030
        c.setAvailable(true);
        return c;
    }

    @Test
    void getAllConductors_shouldReturnListOfConductors() {
        // Arrange: Prepare two conductor objects
        Conductor conductor2 = createTestConductor(2L, "959876543V", "Bimal", "Fernando");
        List<Conductor> conductorList = Arrays.asList(conductor, conductor2);

        // Arrange: Define the behavior of the mocked repository
        when(conductorRepository.findAll()).thenReturn(conductorList);

        // Act: Call the service method
        List<Conductor> result = conductorService.getAllConductors();

        // Assert: Check the result
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(conductorRepository, times(1)).findAll();
    }

    @Test
    void getConductorById_shouldReturnConductor_whenFound() {
        // Arrange: Define the behavior of the mocked repository
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));

        // Act: Call the service method
        Optional<Conductor> result = conductorService.getConductorById(1L);

        // Assert: Check the result
        assertTrue(result.isPresent());
        assertEquals("Amara", result.get().getFirstName());
        verify(conductorRepository, times(1)).findById(1L);
    }

    @Test
    void getConductorById_shouldReturnEmptyOptional_whenNotFound() {
        // Arrange: Define the behavior of the mocked repository
        when(conductorRepository.findById(2L)).thenReturn(Optional.empty());

        // Act: Call the service method
        Optional<Conductor> result = conductorService.getConductorById(2L);

        // Assert: Check the result
        assertFalse(result.isPresent());
        verify(conductorRepository, times(1)).findById(2L);
    }

    @Test
    void getConductorByNic_shouldReturnConductor_whenFound() {
        // Arrange: Define the behavior of the mocked repository
        when(conductorRepository.findByNicNumber("901234567V")).thenReturn(Optional.of(conductor));

        // Act: Call the service method
        Optional<Conductor> result = conductorService.getConductorByNic("901234567V");

        // Assert: Check the result
        assertTrue(result.isPresent());
        assertEquals("Amara", result.get().getFirstName());
        verify(conductorRepository, times(1)).findByNicNumber("901234567V");
    }

    @Test
    void getConductorByNic_shouldReturnEmptyOptional_whenNotFound() {
        // Arrange: Define the behavior of the mocked repository
        when(conductorRepository.findByNicNumber("111111111V")).thenReturn(Optional.empty());

        // Act: Call the service method
        Optional<Conductor> result = conductorService.getConductorByNic("111111111V");

        // Assert: Check the result
        assertFalse(result.isPresent());
        verify(conductorRepository, times(1)).findByNicNumber("111111111V");
    }

    @Test
    void createConductor_shouldReturnSavedConductor() {
        // Arrange: Define the behavior of the mocked repository
        when(conductorRepository.save(any(Conductor.class))).thenReturn(conductor);

        // Act: Call the service method
        Conductor savedConductor = conductorService.createConductor(conductor);

        // Assert: Check the result and verification
        assertNotNull(savedConductor);
        assertEquals("Amara", savedConductor.getFirstName());
        // Verify that the save method was called exactly once with any Conductor object
        verify(conductorRepository, times(1)).save(conductor);
    }

    @Test
    void updateConductor_shouldReturnUpdatedConductor_whenFound() {
        // Arrange: Prepare update details
        Conductor updatedDetails = createTestConductor(1L, "901234567V", "NewName", "UpdatedLast");
        updatedDetails.setContactNumber("0779998888");
        updatedDetails.setAvailable(false);

        // Arrange: Mock the findById and save calls
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(conductorRepository.save(any(Conductor.class))).thenAnswer(i -> i.getArguments()[0]); // Return the saved
                                                                                                   // object

        // Act: Call the service method
        Conductor updatedConductor = conductorService.updateConductor(1L, updatedDetails);

        // Assert: Check if the fields were actually updated
        assertNotNull(updatedConductor);
        assertEquals("NewName", updatedConductor.getFirstName());
        assertEquals("UpdatedLast", updatedConductor.getLastName());
        assertEquals("0779998888", updatedConductor.getContactNumber());
        assertFalse(updatedConductor.isAvailable());

        // Verify save was called on the updated object
        verify(conductorRepository, times(1)).findById(1L);
        verify(conductorRepository, times(1)).save(any(Conductor.class));
    }

    @Test
    void updateConductor_shouldThrowException_whenNotFound() {
        // Arrange: Mock findById to return empty
        when(conductorRepository.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert: Check that ResourceNotFoundException is thrown
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            conductorService.updateConductor(10L, conductor);
        });

        String expectedMessage = "Conductor not found with id: 10";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        // Verify findById was called, and save was NOT called
        verify(conductorRepository, times(1)).findById(10L);
        verify(conductorRepository, never()).save(any(Conductor.class));
    }

    @Test
    void deleteConductor_shouldCallDeleteById() {
        // Act: Call the service method
        conductorService.deleteConductor(1L);

        // Assert: Verify that the deleteById method was called exactly once
        verify(conductorRepository, times(1)).deleteById(1L);
        // Note: We don't need to mock findById or handle ResourceNotFoundException
        // here,
        // as the current service implementation simply calls deleteById without
        // checking for existence first.
    }

}