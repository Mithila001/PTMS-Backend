package com.tritonptms.public_transport_management_system.repository;

import com.tritonptms.public_transport_management_system.model.ScheduledTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledTripRepository
        extends JpaRepository<ScheduledTrip, Long>, JpaSpecificationExecutor<ScheduledTrip> {
}