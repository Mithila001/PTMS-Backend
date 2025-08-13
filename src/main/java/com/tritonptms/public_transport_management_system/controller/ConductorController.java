package com.tritonptms.public_transport_management_system.controller;

import com.tritonptms.public_transport_management_system.model.Conductor;
import com.tritonptms.public_transport_management_system.service.ConductorService;
import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conductors")
public class ConductorController {

    private final ConductorService conductorService;

    public ConductorController(ConductorService conductorService) {
        this.conductorService = conductorService;
    }

    @GetMapping
    public ResponseEntity<List<Conductor>> getAllConductors() {
        List<Conductor> conductors = conductorService.getAllConductors();
        return new ResponseEntity<>(conductors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conductor> getConductorById(@PathVariable Long id) {
        Conductor conductor = conductorService.getConductorById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor not found with id: " + id));
        return new ResponseEntity<>(conductor, HttpStatus.OK);
    }

    @GetMapping("/nic/{nicNumber}")
    public ResponseEntity<Conductor> getConductorByNic(@PathVariable String nicNumber) {
        Conductor conductor = conductorService.getConductorByNic(nicNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor not found with NIC number: " + nicNumber));
        return new ResponseEntity<>(conductor, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Conductor> createConductor(@Valid @RequestBody Conductor conductor) {
        Conductor newConductor = conductorService.createConductor(conductor);
        return new ResponseEntity<>(newConductor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conductor> updateConductor(@PathVariable Long id, @Valid @RequestBody Conductor conductorDetails) {
        Conductor updatedConductor = conductorService.updateConductor(id, conductorDetails);
        return new ResponseEntity<>(updatedConductor, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConductor(@PathVariable Long id) {
        conductorService.deleteConductor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}