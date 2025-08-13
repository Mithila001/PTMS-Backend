package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Conductor;
import java.util.List;
import java.util.Optional;

public interface ConductorService {

    List<Conductor> getAllConductors();

    Optional<Conductor> getConductorById(Long id);

    Optional<Conductor> getConductorByNic(String nicNumber);

    Conductor createConductor(Conductor conductor);

    Conductor updateConductor(Long id, Conductor conductorDetails);

    void deleteConductor(Long id);
}