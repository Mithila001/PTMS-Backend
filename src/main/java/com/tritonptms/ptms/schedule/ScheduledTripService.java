package com.tritonptms.ptms.schedule;

import com.tritonptms.ptms.common.exception.BadRequestException;
import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import com.tritonptms.ptms.route.Route;
import com.tritonptms.ptms.route.RouteRepository;
import com.tritonptms.ptms.schedule.dto.ScheduledTripRequest;
import com.tritonptms.ptms.schedule.dto.ScheduledTripResponse;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@PreAuthorize("hasAnyRole('ADMIN','OPERATIONS_MANAGER')")
public class ScheduledTripService {

    private final ScheduledTripRepository scheduledTripRepository;
    private final RouteRepository routeRepository;
    private final ScheduledTripMapper scheduledTripMapper;

    public ScheduledTripService(ScheduledTripRepository scheduledTripRepository,
            RouteRepository routeRepository,
            ScheduledTripMapper scheduledTripMapper) {
        this.scheduledTripRepository = scheduledTripRepository;
        this.routeRepository = routeRepository;
        this.scheduledTripMapper = scheduledTripMapper;
    }

    public List<ScheduledTripResponse> getAllScheduledTrips() {
        return scheduledTripRepository.findAll().stream().map(scheduledTripMapper::toResponse).toList();
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
        validateTimes(request);
        ScheduledTrip trip = findTrip(id);
        scheduledTripMapper.apply(trip, request);
        trip.setRoute(findRoute(request.routeId()));
        return scheduledTripMapper.toResponse(scheduledTripRepository.save(trip));
    }

    @Transactional
    public void deleteScheduledTrip(Long id) {
        scheduledTripRepository.delete(findTrip(id));
    }

    public List<ScheduledTripResponse> searchScheduledTrips(String routeNumber, Direction direction) {
        Specification<ScheduledTrip> specification = Specification.allOf(
                ScheduledTripSpecification.hasRouteNumber(routeNumber),
                ScheduledTripSpecification.hasDirection(direction));
        return scheduledTripRepository.findAll(specification).stream().map(scheduledTripMapper::toResponse).toList();
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
        if (!request.expectedEndTime().isAfter(request.expectedStartTime())) {
            throw new BadRequestException("expectedEndTime must be after expectedStartTime.");
        }
    }
}
