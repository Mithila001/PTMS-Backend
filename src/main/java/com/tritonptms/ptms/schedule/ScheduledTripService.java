package com.tritonptms.ptms.schedule;

import com.tritonptms.ptms.schedule.dto.ScheduledTripDto;
import com.tritonptms.ptms.route.Route;
import com.tritonptms.ptms.route.RouteRepository;
import com.tritonptms.ptms.route.RouteService;
import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ScheduledTripService {

    private final ScheduledTripRepository scheduledTripRepository;
    private final RouteRepository routeRepository;
    private final RouteService routeService;

    public ScheduledTripService(ScheduledTripRepository scheduledTripRepository, RouteRepository routeRepository,
            RouteService routeService) {
        this.scheduledTripRepository = scheduledTripRepository;
        this.routeRepository = routeRepository;
        this.routeService = routeService;
    }

    public List<ScheduledTripDto> getAllScheduledTrips() {
        return scheduledTripRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<ScheduledTripDto> getScheduledTripById(Long id) {
        return scheduledTripRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional
    public ScheduledTrip saveScheduledTrip(ScheduledTripDto scheduledTripDto) {
        ScheduledTrip scheduledTrip = convertToEntity(scheduledTripDto);
        return scheduledTripRepository.save(scheduledTrip);
    }

    @Transactional
    public void deleteScheduledTrip(Long id) {
        scheduledTripRepository.deleteById(id);
    }

    public ScheduledTripDto convertToDto(ScheduledTrip scheduledTrip) {
        ScheduledTripDto dto = new ScheduledTripDto();
        dto.setId(scheduledTrip.getId());
        dto.setDirection(scheduledTrip.getDirection());
        dto.setExpectedStartTime(scheduledTrip.getExpectedStartTime());
        dto.setExpectedEndTime(scheduledTrip.getExpectedEndTime());

        // Convert the nested Route entity to RouteDto
        if (scheduledTrip.getRoute() != null) {
            dto.setRoute(routeService.convertToDto(scheduledTrip.getRoute()));
        }

        return dto;
    }

    public ScheduledTrip convertToEntity(ScheduledTripDto scheduledTripDto) {
        ScheduledTrip entity = new ScheduledTrip();
        if (scheduledTripDto.getId() != null) {
            entity.setId(scheduledTripDto.getId());
        }
        entity.setDirection(scheduledTripDto.getDirection());
        entity.setExpectedStartTime(scheduledTripDto.getExpectedStartTime());
        entity.setExpectedEndTime(scheduledTripDto.getExpectedEndTime());

        // Use the ID from the DTO to find and set the Route entity
        if (scheduledTripDto.getRoute() != null && scheduledTripDto.getRoute().getId() != null) {
            Route route = routeRepository.findById(scheduledTripDto.getRoute().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Route not found with id: " + scheduledTripDto.getRoute().getId()));
            entity.setRoute(route);
        }

        return entity;
    }

    public List<ScheduledTrip> searchScheduledTrips(String scheduledTripRouteNumber, Direction direction) {
        // Long routeNumber = null;
        // try {
        // if (scheduledTripRouteNumber != null && !scheduledTripRouteNumber.isEmpty())
        // {
        // routeNumber = Long.parseLong(scheduledTripRouteNumber);
        // }
        // } catch (NumberFormatException e) {

        // // If parsing fails, return empty list
        // return Collections.emptyList();
        // }
        Specification<ScheduledTrip> spec = Specification.allOf(
                ScheduledTripSpecification.hasRouteNumber(scheduledTripRouteNumber),
                ScheduledTripSpecification.hasDirection(direction));

        return scheduledTripRepository.findAll(spec);
    }

}
