package com.tritonptms.ptms.employee;

import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ConductorService {

    private final ConductorRepository conductorRepository;

    public ConductorService(ConductorRepository conductorRepository) {
        this.conductorRepository = conductorRepository;
    }
    public List<Conductor> getAllConductors() {
        return conductorRepository.findAll();
    }
    public Optional<Conductor> getConductorById(Long id) {
        return conductorRepository.findById(id);
    }
    public Optional<Conductor> getConductorByNic(String nicNumber) {
        return conductorRepository.findByNicNumber(nicNumber);
    }
    @Transactional
    public Conductor createConductor(Conductor conductor) {
        return conductorRepository.save(conductor);
    }
    @Transactional
    public Conductor updateConductor(Long id, Conductor conductorDetails) {
        Conductor existingConductor = conductorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor not found with id: " + id));
        
        // Update the common employee fields
        existingConductor.setFirstName(conductorDetails.getFirstName());
        existingConductor.setLastName(conductorDetails.getLastName());
        existingConductor.setDateOfBirth(conductorDetails.getDateOfBirth());
        existingConductor.setContactNumber(conductorDetails.getContactNumber());
        existingConductor.setEmail(conductorDetails.getEmail());
        existingConductor.setAddress(conductorDetails.getAddress());
        existingConductor.setDateJoined(conductorDetails.getDateJoined());
        existingConductor.setIsCurrentEmployee(conductorDetails.getIsCurrentEmployee());
        
        // Update the conductor-specific fields
        existingConductor.setConductorLicenseNumber(conductorDetails.getConductorLicenseNumber());
        existingConductor.setLicenseExpirationDate(conductorDetails.getLicenseExpirationDate());
        existingConductor.setAvailable(conductorDetails.isAvailable());

        return conductorRepository.save(existingConductor);
    }
    @Transactional
    public void deleteConductor(Long id) {
        conductorRepository.deleteById(id);
    }
}
