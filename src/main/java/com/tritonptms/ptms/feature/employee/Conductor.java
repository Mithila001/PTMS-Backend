package com.tritonptms.ptms.feature.employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Entity
@Table(name = "conductors", indexes = @Index(name = "idx_conductors_available", columnList = "available"))
public class Conductor extends Employee {

    @NotBlank(message = "Conductor license number is mandatory")
    @Size(min = 8, max = 15, message = "Conductor license number length is not valid")
    @Column(name = "conductor_license_number", nullable = false, unique = true, length = 15)
    private String conductorLicenseNumber;

    @NotNull(message = "License expiration date is mandatory")
    @Temporal(TemporalType.DATE)
    @Column(name = "license_expiration_date", nullable = false)
    private Date licenseExpirationDate;

    @Column(nullable = false)
    private boolean available;

    public String getConductorLicenseNumber() { return conductorLicenseNumber; }
    public void setConductorLicenseNumber(String conductorLicenseNumber) { this.conductorLicenseNumber = conductorLicenseNumber; }
    public Date getLicenseExpirationDate() { return licenseExpirationDate; }
    public void setLicenseExpirationDate(Date licenseExpirationDate) { this.licenseExpirationDate = licenseExpirationDate; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
