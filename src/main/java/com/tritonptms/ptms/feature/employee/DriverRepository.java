package com.tritonptms.ptms.feature.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver> {
    Optional<Driver> findByNicNumber(String nicNumber);

    long countByAvailable(boolean available);

    @Query("select count(d) from Driver d where d.available = true and d.isCurrentEmployee = true")
    long countActiveEmployees();
}
