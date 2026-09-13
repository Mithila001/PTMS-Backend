package com.tritonptms.ptms.feature.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledTripRepository
        extends JpaRepository<ScheduledTrip, Long>, JpaSpecificationExecutor<ScheduledTrip> {
}
