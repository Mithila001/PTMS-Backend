package com.tritonptms.ptms.employee;

import com.tritonptms.ptms.common.audit.AuditableEntity;
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
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Date;

@MappedSuperclass
public abstract class Employee extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "NIC number is mandatory")
    @Size(min = 10, max = 12, message = "NIC number must be 10 or 12 characters")
    @Pattern(regexp = "^([0-9]{9}[vV]|[0-9]{12})$", message = "Invalid NIC number format")
    @Column(name = "nic_number", nullable = false, unique = true, length = 12)
    private String nicNumber;

    @NotBlank(message = "First name is mandatory")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull(message = "Date of birth is mandatory")
    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth", nullable = false)
    private Date dateOfBirth;

    @NotBlank(message = "Contact number is mandatory")
    @Size(min = 10, max = 10, message = "Contact number must be 10 digits")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid contact number format")
    @Column(name = "contact_number", nullable = false, length = 10)
    private String contactNumber;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "Address is mandatory")
    @Column(nullable = false, length = 255)
    private String address;

    @NotNull(message = "Date joined is mandatory")
    @Temporal(TemporalType.DATE)
    @PastOrPresent(message = "Date joined cannot be in the future")
    @Column(name = "date_joined", nullable = false)
    private Date dateJoined;

    @Column(name = "is_current_employee", nullable = false)
    private boolean isCurrentEmployee;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNicNumber() { return nicNumber; }
    public void setNicNumber(String nicNumber) { this.nicNumber = nicNumber; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Date getDateJoined() { return dateJoined; }
    public void setDateJoined(Date dateJoined) { this.dateJoined = dateJoined; }
    public boolean getIsCurrentEmployee() { return isCurrentEmployee; }
    public void setIsCurrentEmployee(boolean isCurrentEmployee) { this.isCurrentEmployee = isCurrentEmployee; }
}
