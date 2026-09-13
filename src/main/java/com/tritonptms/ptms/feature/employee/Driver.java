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
@Table(name = "drivers", indexes = @Index(name = "idx_drivers_available", columnList = "available"))
public class Driver extends Employee {

    @NotBlank(message = "Driving license number is mandatory")
    @Size(min = 8, max = 15, message = "Driving license number length is not valid")
    @Column(name = "driving_license_number", nullable = false, unique = true, length = 15)
    private String drivingLicenseNumber;

    @NotNull(message = "License expiration date is mandatory")
    @Temporal(TemporalType.DATE)
    @Column(name = "license_expiration_date", nullable = false)
    private Date licenseExpirationDate;

    @NotBlank(message = "License class is mandatory")
    @Column(name = "license_class", nullable = false, length = 30)
    private String licenseClass;

    @Size(max = 15, message = "NTC license number length is not valid")
    @Column(name = "ntc_license_number", unique = true, length = 15)
    private String ntcLicenseNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "ntc_license_expiration_date")
    private Date ntcLicenseExpirationDate;

    @Column(nullable = false)
    private boolean available;

    public String getDrivingLicenseNumber() { return drivingLicenseNumber; }
    public void setDrivingLicenseNumber(String drivingLicenseNumber) { this.drivingLicenseNumber = drivingLicenseNumber; }
    public Date getLicenseExpirationDate() { return licenseExpirationDate; }
    public void setLicenseExpirationDate(Date licenseExpirationDate) { this.licenseExpirationDate = licenseExpirationDate; }
    public String getLicenseClass() { return licenseClass; }
    public void setLicenseClass(String licenseClass) { this.licenseClass = licenseClass; }
    public String getNtcLicenseNumber() { return ntcLicenseNumber; }
    public void setNtcLicenseNumber(String ntcLicenseNumber) { this.ntcLicenseNumber = ntcLicenseNumber; }
    public Date getNtcLicenseExpirationDate() { return ntcLicenseExpirationDate; }
    public void setNtcLicenseExpirationDate(Date ntcLicenseExpirationDate) { this.ntcLicenseExpirationDate = ntcLicenseExpirationDate; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
