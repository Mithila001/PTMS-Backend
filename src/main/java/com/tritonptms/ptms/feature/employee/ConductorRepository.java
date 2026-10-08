package com.tritonptms.ptms.feature.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

@Repository
public interface ConductorRepository extends JpaRepository<Conductor, Long>, JpaSpecificationExecutor<Conductor> {
    Optional<Conductor> findByNicNumber(String nicNumber);

    long countByAvailable(boolean available);

    @Query("select count(c) from Conductor c where c.available = true and c.isCurrentEmployee = true")
    long countActiveEmployees();
}
