package com.tritonptms.ptms.bus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long>, JpaSpecificationExecutor<Bus> {
    long countByIsActive(boolean isActive);

}
