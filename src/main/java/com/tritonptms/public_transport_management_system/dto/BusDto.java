package com.tritonptms.public_transport_management_system.dto;

import com.tritonptms.public_transport_management_system.model.Bus.ComfortType;
import com.tritonptms.public_transport_management_system.model.Bus.ServiceType;
import com.tritonptms.public_transport_management_system.model.Vehicle.FuelType;

public class BusDto {
    private Long id;
    private String registrationNumber;
    private String make;
    private String model;
    private int yearOfManufacture;
    private FuelType fuelType;
    private boolean isActive;
    private Integer seatingCapacity;
    private Integer standingCapacity;
    private Long ntcPermitNumber;
    private ComfortType comfortType;
    private Boolean isA_C;
    private ServiceType serviceType;

    // Default constructor
    public BusDto() {
    }

    // Getters and setters for all fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(int yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

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
}