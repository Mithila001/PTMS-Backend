package com.tritonptms.public_transport_management_system.repository;

import com.tritonptms.public_transport_management_system.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver> {
    Optional<Driver> findByNicNumber(String nicNumber);
}