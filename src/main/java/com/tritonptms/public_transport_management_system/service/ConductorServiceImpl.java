package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Conductor;
import com.tritonptms.public_transport_management_system.repository.ConductorRepository;
import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConductorServiceImpl implements ConductorService {

    private final ConductorRepository conductorRepository;

    public ConductorServiceImpl(ConductorRepository conductorRepository) {
        this.conductorRepository = conductorRepository;
    }

    @Override
    public List<Conductor> getAllConductors() {
        return conductorRepository.findAll();
    }

    @Override
    public Optional<Conductor> getConductorById(Long id) {
        return conductorRepository.findById(id);
    }

    @Override
    public Optional<Conductor> getConductorByNic(String nicNumber) {
        return conductorRepository.findByNicNumber(nicNumber);
    }

    @Override
    public Conductor createConductor(Conductor conductor) {
        return conductorRepository.save(conductor);
    }

    @Override
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

    @Override
    public void deleteConductor(Long id) {
        conductorRepository.deleteById(id);
    }
}