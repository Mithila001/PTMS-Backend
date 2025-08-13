package com.tritonptms.public_transport_management_system.repository;

import com.tritonptms.public_transport_management_system.model.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ConductorRepository extends JpaRepository<Conductor, Long> {
    Optional<Conductor> findByNicNumber(String nicNumber);
}