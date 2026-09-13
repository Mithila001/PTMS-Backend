package com.tritonptms.ptms.bus;

import com.tritonptms.ptms.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

@Entity
@Audited
@AuditOverride(forClass = AuditableEntity.class, isAudited = true)
@Table(name = "buses", indexes = {
        @Index(name = "idx_buses_active", columnList = "is_active"),
        @Index(name = "idx_buses_service_type", columnList = "service_type")
})
public class Bus extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_number", nullable = false, unique = true, length = 15)
    private String registrationNumber;

    @Column(nullable = false, length = 50)
    private String make;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(name = "year_of_manufacture", nullable = false)
    private int yearOfManufacture;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false, length = 20)
    private FuelType fuelType;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @NotNull(message = "Seating capacity cannot be null")
    @Min(value = 1, message = "Seating capacity must be at least 1")
    @Max(value = 100, message = "Seating capacity cannot exceed 100")
    @Column(name = "seating_capacity", nullable = false)
    private Integer seatingCapacity;

    @NotNull(message = "Standing capacity cannot be null")
    @Min(value = 0, message = "Standing capacity must be at least 0")
    @Max(value = 50, message = "Standing capacity cannot exceed 50")
    @Column(name = "standing_capacity", nullable = false)
    private Integer standingCapacity;

    @NotNull(message = "NTC permit number is mandatory")
    @Min(value = 1000000000, message = "NTC permit number must be at least 10 digits")
    @Column(name = "ntc_permit_number", nullable = false, unique = true)
    private Long ntcPermitNumber;

    @NotNull(message = "Comfort Type is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "comfort_type", nullable = false, length = 20)
    private ComfortType comfortType;

    @NotNull(message = "isA_C cannot be null")
    @Column(name = "is_ac", nullable = false)
    private Boolean airConditioned;

    @NotNull(message = "Service Type is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 20)
    private ServiceType serviceType;

    public enum FuelType {
        DIESEL, PETROL, ELECTRIC, HYBRID
    }

    public enum ComfortType {
        NORMAL, SEMI_LUXURY, LUXURY, SUPER_LUXURY
    }

    public enum ServiceType {
        PRIVATE_BUS, CTB_BUS, MINI_BUS
    }

    public Bus() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYearOfManufacture() { return yearOfManufacture; }
    public void setYearOfManufacture(int yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; }
    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public Integer getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(Integer seatingCapacity) { this.seatingCapacity = seatingCapacity; }
    public Integer getStandingCapacity() { return standingCapacity; }
    public void setStandingCapacity(Integer standingCapacity) { this.standingCapacity = standingCapacity; }
    public Long getNtcPermitNumber() { return ntcPermitNumber; }
    public void setNtcPermitNumber(Long ntcPermitNumber) { this.ntcPermitNumber = ntcPermitNumber; }
    public ComfortType getComfortType() { return comfortType; }
    public void setComfortType(ComfortType comfortType) { this.comfortType = comfortType; }
    public Boolean getIsA_C() { return airConditioned; }
    public void setIsA_C(Boolean isA_C) { this.airConditioned = isA_C; }
    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }
}
