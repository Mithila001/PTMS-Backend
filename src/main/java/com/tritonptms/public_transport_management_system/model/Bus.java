package com.tritonptms.public_transport_management_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import com.tritonptms.public_transport_management_system.model.Vehicle;

@Entity
@Table(name = "buses")
public class Bus extends Vehicle {
    @Column(nullable = false)
    private int seatingCapacity;

    @Column(nullable = false)
    private int standingCapacity;

    @Column(nullable = false)
    private int ntcPermitNumber; // National Transport Commission permit number

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private BusType busType; // e.g., NORMAL, SEMI_LUXURY, SUPER_LUXURY

    @Column(nullable = false)
    private boolean isA_C; // To distinguish A/C from non-A/C buses

    public Bus() {
    }

    public enum BusType {
        NORMAL, SEMI_LUXURY, LUXURY, SUPER_LUXURY
    }

    // Getters and Setters...

    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(int seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public int getStandingCapacity() {
        return standingCapacity;
    }

    public void setStandingCapacity(int standingCapacity) {
        this.standingCapacity = standingCapacity;
    }

    public int getNtcPermitNumber() {
        return ntcPermitNumber;
    }

    public void setNtcPermitNumber(int ntcPermitNumber) {
        this.ntcPermitNumber = ntcPermitNumber;
    }

    public BusType getBusType() {
        return busType;
    }

    public void setBusType(BusType busType) {
        this.busType = busType;
    }

    public boolean isA_C() {
        return isA_C;
    }

    public void setA_C(boolean isA_C) {
        this.isA_C = isA_C;
    }

    // ===========

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