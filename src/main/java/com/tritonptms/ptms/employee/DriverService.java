package com.tritonptms.ptms.employee;

import com.tritonptms.ptms.common.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
    public Optional<Driver> getDriverById(Long id) {
        return driverRepository.findById(id);
    }
    public Optional<Driver> getDriverByNic(String nicNumber) {
        return driverRepository.findByNicNumber(nicNumber);
    }
    @Transactional
    public Driver createDriver(Driver driver) {
        return driverRepository.save(driver);
    }
    @Transactional
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
    @Transactional
    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }

}
