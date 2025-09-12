package com.tritonptms.public_transport_management_system.model;

import java.util.Date;

import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

@MappedSuperclass
@Audited
public abstract class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "NIC number is mandatory")
    @Size(min = 10, max = 12, message = "NIC number must be 10 or 12 characters")
    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^([0-9]{9}[vV]|[0-9]{12})$", message = "Invalid NIC number format")
    private String nicNumber;

    @NotBlank(message = "First name is mandatory")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Column(nullable = false)
    private String lastName;

    @NotNull(message = "Date of birth is mandatory")
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date dateOfBirth;

    @NotBlank(message = "Contact number is mandatory")
    @Size(min = 10, max = 10, message = "Contact number must be 10 digits")
    @Column(nullable = false)
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid contact number format")
    private String contactNumber;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Address is mandatory")
    @Column(nullable = false)
    private String address;

    @NotNull(message = "Date joined is mandatory")
    @Temporal(TemporalType.DATE)
    @PastOrPresent(message = "Date joined cannot be in the future")
    @Column(nullable = false)
    private Date dateJoined;

    @Column(nullable = false)
    private boolean isCurrentEmployee;

    // Getters and setters
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

    public boolean getIsCurrentEmployee() {
        return isCurrentEmployee;
    }

    public void setIsCurrentEmployee(boolean isCurrentEmployee) {
        this.isCurrentEmployee = isCurrentEmployee;
    }
}