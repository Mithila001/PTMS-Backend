package com.tritonptms.public_transport_management_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.tritonptms.public_transport_management_system.model.Vehicle;
import com.tritonptms.public_transport_management_system.model.enums.bus.BusType;

@Entity
@Audited
@Table(name = "buses")
public class Bus extends Vehicle {

    @NotNull(message = "Seating capacity cannot be null")
    @Min(value = 1, message = "Seating capacity must be at least 1")
    @Max(value = 100, message = "Seating capacity cannot exceed 100")
    @Column(nullable = false)
    private Integer seatingCapacity;

    @NotNull(message = "Standing capacity cannot be null")
    @Min(value = 0, message = "Standing capacity must be at least 0")
    @Max(value = 50, message = "Standing capacity cannot exceed 50")
    @Column(nullable = false)
    private Integer standingCapacity;

    @NotNull(message = "NTC permit number is mandatory")
    @Min(value = 1000000000, message = "NTC permit number must be at least 10 digits")
    @Column(nullable = false)
    private Long ntcPermitNumber; // National Transport Commission permit number

    @NotNull(message = "Comfort Type is mandatory")
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private ComfortType comfortType; // e.g., NORMAL, SEMI_LUXURY, SUPER_LUXURY

    @NotNull(message = "isA_C cannot be null")
    @Column(nullable = false)
    private Boolean isA_C; // To distinguish A/C from non-A/C buses

    @NotNull(message = "Service Type is mandatory")
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType; // e.g., PRIVATE_BUS, CTB_BUS, LUXURY_BUS, MINI_BUS

    public Bus() {
    }

    public enum ComfortType {
        NORMAL, SEMI_LUXURY, LUXURY, SUPER_LUXURY
    }

    public enum ServiceType {
        PRIVATE_BUS, CTB_BUS, MINI_BUS
    }

    // Getters and Setters...

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public Integer getStandingCapacity() {
        return standingCapacity;
    }

    public void setStandingCapacity(Integer standingCapacity) {
        this.standingCapacity = standingCapacity;
    }

    public Long getNtcPermitNumber() {
        return ntcPermitNumber;
    }

    public void setNtcPermitNumber(Long ntcPermitNumber) {
        this.ntcPermitNumber = ntcPermitNumber;
    }

    public ComfortType getComfortType() {
        return comfortType;
    }

    public void setComfortType(ComfortType comfortType) {
        this.comfortType = comfortType;
    }

    public Boolean getIsA_C() {
        return isA_C;
    }

    public void setIsA_C(Boolean isA_C) {
        this.isA_C = isA_C;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    // Vehicle Parent ===========

    public Long getId() {
        return super.getId();
    }

    public void setId(Long id) {
        super.setId(id);
    }

    public String getRegistrationNumber() {
        return super.getRegistrationNumber();
    }

    public void setRegistrationNumber(String registrationNumber) {
        super.setRegistrationNumber(registrationNumber);
    }

    public String getMake() {
        return super.getMake();
    }

    public void setMake(String make) {
        super.setMake(make);
    }

    public String getModel() {
        return super.getModel();
    }

    public void setModel(String model) {
        super.setModel(model);
    }

    public int getYearOfManufacture() {
        return super.getYearOfManufacture();
    }

    public void setYearOfManufacture(int yearOfManufacture) {
        super.setYearOfManufacture(yearOfManufacture);
    }

    public Vehicle.FuelType getFuelType() {
        return super.getFuelType();
    }

    public void setFuelType(FuelType fuelType) {
        super.setFuelType(fuelType);
    }

    public boolean isActive() {
        return super.isActive();
    }

    public void setActive(boolean active) {
        super.setActive(active);
    }
}