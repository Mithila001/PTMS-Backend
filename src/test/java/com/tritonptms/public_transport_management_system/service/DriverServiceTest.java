package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import com.tritonptms.public_transport_management_system.model.Driver;
import com.tritonptms.public_transport_management_system.repository.DriverRepository;
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
class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverServiceImpl driverService;

    private Driver driver;

    @BeforeEach
    void setUp() {
        driver = new Driver();
        driver.setId(1L);
        driver.setNicNumber("901234567V");
        driver.setFirstName("Kamal");
        driver.setDrivingLicenseNumber("D123456789");
    }

    @Test
    @DisplayName("Should return all drivers")
    void getAllDrivers() {
        when(driverRepository.findAll()).thenReturn(List.of(driver));
        List<Driver> drivers = driverService.getAllDrivers();
        assertNotNull(drivers);
        assertEquals(1, drivers.size());
        verify(driverRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return a driver by ID")
    void getDriverById() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        Optional<Driver> foundDriver = driverService.getDriverById(1L);
        assertTrue(foundDriver.isPresent());
        assertEquals("Kamal", foundDriver.get().getFirstName());
    }

    @Test
    @DisplayName("Should return a driver by NIC number")
    void getDriverByNic() {
        when(driverRepository.findByNicNumber("901234567V")).thenReturn(Optional.of(driver));
        Optional<Driver> foundDriver = driverService.getDriverByNic("901234567V");
        assertTrue(foundDriver.isPresent());
        assertEquals("D123456789", foundDriver.get().getDrivingLicenseNumber());
    }

    @Test
    @DisplayName("Should create a new driver")
    void createDriver() {
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);
        Driver savedDriver = driverService.createDriver(driver);
        assertNotNull(savedDriver);
        assertEquals("901234567V", savedDriver.getNicNumber());
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    @DisplayName("Should update an existing driver")
    void updateDriver() {
        Driver updatedDriverDetails = new Driver();
        updatedDriverDetails.setFirstName("Sunil");
        updatedDriverDetails.setLastName("Perera");
        updatedDriverDetails.setDrivingLicenseNumber("D987654321");

        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(updatedDriverDetails);

        Driver updated = driverService.updateDriver(1L, updatedDriverDetails);

        assertNotNull(updated);
        assertEquals("Sunil", updated.getFirstName());
        assertEquals("D987654321", updated.getDrivingLicenseNumber());
    }
    
    @Test
    @DisplayName("Should throw exception when updating a non-existent driver")
    void updateDriver_throwsException() {
        when(driverRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> driverService.updateDriver(99L, new Driver()));
    }

    @Test
    @DisplayName("Should delete a driver by ID")
    void deleteDriver() {
        doNothing().when(driverRepository).deleteById(1L);
        driverService.deleteDriver(1L);
        verify(driverRepository, times(1)).deleteById(1L);
    }
}