package com.tritonptms.ptms.feature.employee;

import com.tritonptms.ptms.feature.employee.dto.ConductorRequest;
import com.tritonptms.ptms.feature.employee.dto.ConductorResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conductors")
public class ConductorController {

    private final ConductorService conductorService;

    public ConductorController(ConductorService conductorService) {
        this.conductorService = conductorService;
    }

    @GetMapping
    public List<ConductorResponse> getAllConductors() {
        return conductorService.getAllConductors();
    }

    @GetMapping("/{id}")
    public ConductorResponse getConductorById(@PathVariable Long id) {
        return conductorService.getConductorById(id);
    }

    @GetMapping("/nic/{nicNumber}")
    public ConductorResponse getConductorByNic(@PathVariable String nicNumber) {
        return conductorService.getConductorByNic(nicNumber);
    }

    @PostMapping
    public ResponseEntity<ConductorResponse> createConductor(@Valid @RequestBody ConductorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conductorService.createConductor(request));
    }

    @PutMapping("/{id}")
    public ConductorResponse updateConductor(@PathVariable Long id, @Valid @RequestBody ConductorRequest request) {
        return conductorService.updateConductor(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConductor(@PathVariable Long id) {
        conductorService.deleteConductor(id);
        return ResponseEntity.noContent().build();
    }
}
