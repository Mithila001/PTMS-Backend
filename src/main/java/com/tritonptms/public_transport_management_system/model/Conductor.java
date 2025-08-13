package com.tritonptms.public_transport_management_system.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "conductors")
public class Conductor extends Employee {

    @NotBlank(message = "Conductor license number is mandatory")
    @Size(min = 8, max = 15, message = "Conductor license number length is not valid")
    @Column(nullable = false, unique = true)
    private String conductorLicenseNumber;

    @NotNull(message = "License expiration date is mandatory")
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date licenseExpirationDate;

    @Column(nullable = false)
    private boolean available;

    // Getters and setters for Conductor-specific fields
    public String getConductorLicenseNumber() {
        return conductorLicenseNumber;
    }

    public void setConductorLicenseNumber(String conductorLicenseNumber) {
        this.conductorLicenseNumber = conductorLicenseNumber;
    }

    public Date getLicenseExpirationDate() {
        return licenseExpirationDate;
    }

    public void setLicenseExpirationDate(Date licenseExpirationDate) {
        this.licenseExpirationDate = licenseExpirationDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}