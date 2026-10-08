package com.tritonptms.ptms.feature.employee;

import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import com.tritonptms.ptms.feature.employee.dto.ConductorRequest;
import com.tritonptms.ptms.feature.employee.dto.ConductorResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@PreAuthorize("hasAnyRole('ADMIN','OPERATIONS_MANAGER')")
public class ConductorService {

    private final ConductorRepository conductorRepository;
    private final ConductorMapper conductorMapper;

    public ConductorService(ConductorRepository conductorRepository, ConductorMapper conductorMapper) {
        this.conductorRepository = conductorRepository;
        this.conductorMapper = conductorMapper;
    }

    public Page<ConductorResponse> getAllConductors(Pageable pageable) {
        return conductorRepository.findAll(pageable).map(conductorMapper::toResponse);
    }

    public ConductorResponse getConductorById(Long id) {
        return conductorMapper.toResponse(findConductor(id));
    }

    public ConductorResponse getConductorByNic(String nicNumber) {
        Conductor conductor = conductorRepository.findByNicNumber(nicNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor not found with NIC: " + nicNumber));
        return conductorMapper.toResponse(conductor);
    }

    @Transactional
    public ConductorResponse createConductor(ConductorRequest request) {
        return conductorMapper.toResponse(conductorRepository.save(conductorMapper.fromRequest(request)));
    }

    @Transactional
    public ConductorResponse updateConductor(Long id, ConductorRequest request) {
        Conductor conductor = findConductor(id);
        conductorMapper.apply(conductor, request);
        return conductorMapper.toResponse(conductorRepository.save(conductor));
    }

    @Transactional
    public void deleteConductor(Long id) {
        conductorRepository.delete(findConductor(id));
    }

    private Conductor findConductor(Long id) {
        return conductorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor not found with id: " + id));
    }
}
