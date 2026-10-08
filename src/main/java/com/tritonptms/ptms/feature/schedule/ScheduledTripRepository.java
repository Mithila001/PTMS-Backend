package com.tritonptms.ptms.feature.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.domain.Specification;

@Repository
public interface ScheduledTripRepository
        extends JpaRepository<ScheduledTrip, Long>, JpaSpecificationExecutor<ScheduledTrip> {

    @Override
    @EntityGraph(attributePaths = "route")
    Page<ScheduledTrip> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "route")
    Page<ScheduledTrip> findAll(Specification<ScheduledTrip> specification, Pageable pageable);
}
