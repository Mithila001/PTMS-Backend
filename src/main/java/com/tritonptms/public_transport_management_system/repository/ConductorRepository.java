package com.tritonptms.public_transport_management_system.repository;

import com.tritonptms.public_transport_management_system.model.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ConductorRepository extends JpaRepository<Conductor, Long>, JpaSpecificationExecutor<Conductor> {
    Optional<Conductor> findByNicNumber(String nicNumber);

    long countByAvailable(boolean available);
}