package com.tritonptms.ptms.feature.assignment;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.domain.Specification;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long>, JpaSpecificationExecutor<Assignment> {
    List<Assignment> findByStatus(AssignmentStatus status);

    @Query("select count(a) from Assignment a where (a.status = com.tritonptms.ptms.feature.assignment.AssignmentStatus.SCHEDULED and a.date >= :today) or a.status = com.tritonptms.ptms.feature.assignment.AssignmentStatus.IN_PROGRESS")
    long countDashboardActive(@Param("today") LocalDate today);

    @Query("select a from Assignment a where a.date between :fromDate and :toDate and a.status <> com.tritonptms.ptms.feature.assignment.AssignmentStatus.CANCELED and a.id <> :excludeId and (a.scheduledTrip.id = :tripId or a.bus.id = :busId or a.driver.id = :driverId or a.conductor.id = :conductorId)")
    List<Assignment> findBookingCandidates(@Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate, @Param("excludeId") Long excludeId,
            @Param("tripId") Long tripId, @Param("busId") Long busId,
            @Param("driverId") Long driverId, @Param("conductorId") Long conductorId);

    boolean existsByScheduledTrip_IdAndStatusNot(Long tripId, AssignmentStatus status);

    @Override
    @EntityGraph(attributePaths = {"scheduledTrip", "bus", "driver", "conductor"})
    Page<Assignment> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"scheduledTrip", "bus", "driver", "conductor"})
    Page<Assignment> findAll(Specification<Assignment> specification, Pageable pageable);
}
