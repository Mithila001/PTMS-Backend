package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Employee;
import com.tritonptms.public_transport_management_system.model.Driver;
import com.tritonptms.public_transport_management_system.model.Conductor;

import java.util.List;

public interface EmployeeService {
    List<Employee> searchEmployees(String nicNumber, String name, String contactNumber, String licenseNumber);

    List<Driver> searchDrivers(String nicNumber, String name, String contactNumber, String licenseNumber);

    List<Conductor> searchConductors(String nicNumber, String name, String contactNumber, String licenseNumber);
}