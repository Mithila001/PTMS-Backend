package com.tritonptms.public_transport_management_system.dto;

import com.tritonptms.public_transport_management_system.model.Employee;

import java.util.Date;

public class EmployeeDto {
    private Long id;
    private String nicNumber;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String contactNumber;
    private String email;
    private String address;
    private Date dateJoined;
    private boolean isCurrentEmployee;
    // Driver-specific fields, you may add them here if needed
    // private String drivingLicenseNumber;

    // A no-argument constructor is needed for Spring to create the object.
    public EmployeeDto() {
    }

    // A helpful method to convert from an Employee entity to DTO.
    public static EmployeeDto fromEntity(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setNicNumber(employee.getNicNumber());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setDateOfBirth(employee.getDateOfBirth());
        dto.setContactNumber(employee.getContactNumber());
        dto.setEmail(employee.getEmail());
        dto.setAddress(employee.getAddress());
        dto.setDateJoined(employee.getDateJoined());
        dto.setCurrentEmployee(employee.getIsCurrentEmployee());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNicNumber() {
        return nicNumber;
    }

    public void setNicNumber(String nicNumber) {
        this.nicNumber = nicNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Date dateJoined) {
        this.dateJoined = dateJoined;
    }

    public boolean isCurrentEmployee() {
        return isCurrentEmployee;
    }

    public void setCurrentEmployee(boolean isCurrentEmployee) {
        this.isCurrentEmployee = isCurrentEmployee;
    }
}