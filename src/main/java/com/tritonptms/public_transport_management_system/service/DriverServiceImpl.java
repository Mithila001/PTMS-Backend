package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Driver;
import com.tritonptms.public_transport_management_system.repository.DriverRepository;
import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Override
    public Optional<Driver> getDriverById(Long id) {
        return driverRepository.findById(id);
    }

    @Override
    public Optional<Driver> getDriverByNic(String nicNumber) {
        return driverRepository.findByNicNumber(nicNumber);
    }

    @Override
    public Driver createDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    @Override
    public Driver updateDriver(Long id, Driver driverDetails) {
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        // Update the common employee fields
        existingDriver.setFirstName(driverDetails.getFirstName());
        existingDriver.setLastName(driverDetails.getLastName());
        existingDriver.setDateOfBirth(driverDetails.getDateOfBirth());
        existingDriver.setContactNumber(driverDetails.getContactNumber());
        existingDriver.setEmail(driverDetails.getEmail());
        existingDriver.setAddress(driverDetails.getAddress());
        existingDriver.setDateJoined(driverDetails.getDateJoined());
        existingDriver.setIsCurrentEmployee(driverDetails.getIsCurrentEmployee());
        
        // Update the driver-specific fields
        existingDriver.setDrivingLicenseNumber(driverDetails.getDrivingLicenseNumber());
        existingDriver.setLicenseExpirationDate(driverDetails.getLicenseExpirationDate());
        existingDriver.setLicenseClass(driverDetails.getLicenseClass());
        existingDriver.setNtcLicenseNumber(driverDetails.getNtcLicenseNumber());
        existingDriver.setNtcLicenseExpirationDate(driverDetails.getNtcLicenseExpirationDate());
        existingDriver.setAvailable(driverDetails.isAvailable());

        return driverRepository.save(existingDriver);
    }

    @Override
    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }
}