package com.tritonptms.ptms.feature.schedule;

import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import com.tritonptms.ptms.feature.route.Route;
import com.tritonptms.ptms.feature.route.RouteRepository;
import com.tritonptms.ptms.feature.schedule.dto.ScheduledTripRequest;
import com.tritonptms.ptms.feature.schedule.dto.ScheduledTripResponse;
import com.tritonptms.ptms.feature.assignment.AssignmentRepository;
import com.tritonptms.ptms.feature.assignment.AssignmentStatus;
import com.tritonptms.ptms.infrastructure.persistence.SchedulingWriteLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@PreAuthorize("hasAnyRole('ADMIN','OPERATIONS_MANAGER')")
public class ScheduledTripService {

    private final ScheduledTripRepository scheduledTripRepository;
    private final RouteRepository routeRepository;
    private final ScheduledTripMapper scheduledTripMapper;
    private final AssignmentRepository assignmentRepository;
    private final SchedulingWriteLock schedulingWriteLock;

    public ScheduledTripService(ScheduledTripRepository scheduledTripRepository,
            RouteRepository routeRepository,
            ScheduledTripMapper scheduledTripMapper,
            AssignmentRepository assignmentRepository,
            SchedulingWriteLock schedulingWriteLock) {
        this.scheduledTripRepository = scheduledTripRepository;
        this.routeRepository = routeRepository;
        this.scheduledTripMapper = scheduledTripMapper;
        this.assignmentRepository = assignmentRepository;
        this.schedulingWriteLock = schedulingWriteLock;
    }

    public Page<ScheduledTripResponse> getAllScheduledTrips(Pageable pageable) {
        return scheduledTripRepository.findAll(pageable).map(scheduledTripMapper::toResponse);
    }

    public ScheduledTripResponse getScheduledTripById(Long id) {
        return scheduledTripMapper.toResponse(findTrip(id));
    }

    @Transactional
    public ScheduledTripResponse createScheduledTrip(ScheduledTripRequest request) {
        validateTimes(request);
        ScheduledTrip trip = scheduledTripMapper.fromRequest(request);
        trip.setRoute(findRoute(request.routeId()));
        return scheduledTripMapper.toResponse(scheduledTripRepository.save(trip));
    }

    @Transactional
    public ScheduledTripResponse updateScheduledTrip(Long id, ScheduledTripRequest request) {
        schedulingWriteLock.acquire();
        validateTimes(request);
        ScheduledTrip trip = findTrip(id);
        boolean changed = !trip.getRoute().getId().equals(request.routeId())
                || trip.getDirection() != request.direction()
                || !trip.getExpectedStartTime().equals(request.expectedStartTime())
                || !trip.getExpectedEndTime().equals(request.expectedEndTime());
        if (changed && assignmentRepository.existsByScheduledTrip_IdAndStatusNot(id, AssignmentStatus.CANCELED)) {
            throw new com.tritonptms.ptms.common.exception.ConflictException(
                    "This trip has assignments. Create a new scheduled trip to change its definition.");
        }
        scheduledTripMapper.apply(trip, request);
        trip.setRoute(findRoute(request.routeId()));
        return scheduledTripMapper.toResponse(scheduledTripRepository.save(trip));
    }

    @Transactional
    public void deleteScheduledTrip(Long id) {
        schedulingWriteLock.acquire();
        scheduledTripRepository.delete(findTrip(id));
    }

    public Page<ScheduledTripResponse> searchScheduledTrips(String routeNumber, Direction direction, Pageable pageable) {
        Specification<ScheduledTrip> specification = Specification.allOf(
                ScheduledTripSpecification.hasRouteNumber(routeNumber),
                ScheduledTripSpecification.hasDirection(direction));
        return scheduledTripRepository.findAll(specification, pageable).map(scheduledTripMapper::toResponse);
    }

    private ScheduledTrip findTrip(Long id) {
        return scheduledTripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scheduled trip not found with id: " + id));
    }

    private Route findRoute(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
    }

    private void validateTimes(ScheduledTripRequest request) {
        TripTime.interval(LocalDate.of(2000, 1, 1), request.expectedStartTime(), request.expectedEndTime());
    }
}
