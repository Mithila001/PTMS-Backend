package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Driver;
import java.util.List;
import java.util.Optional;

public interface DriverService {

    List<Driver> getAllDrivers();

    Optional<Driver> getDriverById(Long id);

    Optional<Driver> getDriverByNic(String nicNumber);

    Driver createDriver(Driver driver);

    Driver updateDriver(Long id, Driver driverDetails);

    void deleteDriver(Long id);
}