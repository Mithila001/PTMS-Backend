package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import com.tritonptms.public_transport_management_system.model.Conductor;
import com.tritonptms.public_transport_management_system.repository.ConductorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConductorServiceImplTest {

    @Mock
    private ConductorRepository conductorRepository;

    @InjectMocks
    private ConductorServiceImpl conductorService;

    private Conductor conductor;

    @BeforeEach
    void setUp() {
        conductor = new Conductor();
        conductor.setId(1L);
        conductor.setNicNumber("851234567V");
        conductor.setFirstName("Samal");
        conductor.setConductorLicenseNumber("C987654321");
    }

    @Test
    @DisplayName("Should return all conductors")
    void getAllConductors() {
        when(conductorRepository.findAll()).thenReturn(List.of(conductor));
        List<Conductor> conductors = conductorService.getAllConductors();
        assertNotNull(conductors);
        assertEquals(1, conductors.size());
        verify(conductorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return a conductor by ID")
    void getConductorById() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        Optional<Conductor> foundConductor = conductorService.getConductorById(1L);
        assertTrue(foundConductor.isPresent());
        assertEquals("Samal", foundConductor.get().getFirstName());
    }
    
    @Test
    @DisplayName("Should return a conductor by NIC number")
    void getConductorByNic() {
        when(conductorRepository.findByNicNumber("851234567V")).thenReturn(Optional.of(conductor));
        Optional<Conductor> foundConductor = conductorService.getConductorByNic("851234567V");
        assertTrue(foundConductor.isPresent());
        assertEquals("C987654321", foundConductor.get().getConductorLicenseNumber());
    }

    @Test
    @DisplayName("Should create a new conductor")
    void createConductor() {
        when(conductorRepository.save(any(Conductor.class))).thenReturn(conductor);
        Conductor savedConductor = conductorService.createConductor(conductor);
        assertNotNull(savedConductor);
        assertEquals("851234567V", savedConductor.getNicNumber());
        verify(conductorRepository, times(1)).save(conductor);
    }

    @Test
    @DisplayName("Should update an existing conductor")
    void updateConductor() {
        Conductor updatedConductorDetails = new Conductor();
        updatedConductorDetails.setFirstName("Ajith");
        updatedConductorDetails.setLastName("Fernando");
        updatedConductorDetails.setConductorLicenseNumber("C112233445");

        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(conductorRepository.save(any(Conductor.class))).thenReturn(updatedConductorDetails);

        Conductor updated = conductorService.updateConductor(1L, updatedConductorDetails);

        assertNotNull(updated);
        assertEquals("Ajith", updated.getFirstName());
        assertEquals("C112233445", updated.getConductorLicenseNumber());
    }
    
    @Test
    @DisplayName("Should throw exception when updating a non-existent conductor")
    void updateConductor_throwsException() {
        when(conductorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> conductorService.updateConductor(99L, new Conductor()));
    }

    @Test
    @DisplayName("Should delete a conductor by ID")
    void deleteConductor() {
        doNothing().when(conductorRepository).deleteById(1L);
        conductorService.deleteConductor(1L);
        verify(conductorRepository, times(1)).deleteById(1L);
    }
}