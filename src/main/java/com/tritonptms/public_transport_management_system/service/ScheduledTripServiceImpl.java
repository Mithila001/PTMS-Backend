package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.dto.ScheduledTripDto;
import com.tritonptms.public_transport_management_system.model.Route;
import com.tritonptms.public_transport_management_system.model.ScheduledTrip;
import com.tritonptms.public_transport_management_system.model.enums.route.Direction;
import com.tritonptms.public_transport_management_system.repository.ScheduledTripRepository;
import com.tritonptms.public_transport_management_system.service.specification.ScheduledTripSpecification;
import com.tritonptms.public_transport_management_system.repository.RouteRepository;
import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduledTripServiceImpl implements ScheduledTripService {

    private final ScheduledTripRepository scheduledTripRepository;
    private final RouteRepository routeRepository;
    private final RouteService routeService;

    @Autowired
    public ScheduledTripServiceImpl(ScheduledTripRepository scheduledTripRepository, RouteRepository routeRepository,
            RouteService routeService) {
        this.scheduledTripRepository = scheduledTripRepository;
        this.routeRepository = routeRepository;
        this.routeService = routeService;
    }

    @Override
    public List<ScheduledTripDto> getAllScheduledTrips() {
        return scheduledTripRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ScheduledTripDto> getScheduledTripById(Long id) {
        return scheduledTripRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public ScheduledTrip saveScheduledTrip(ScheduledTripDto scheduledTripDto) {
        ScheduledTrip scheduledTrip = convertToEntity(scheduledTripDto);
        return scheduledTripRepository.save(scheduledTrip);
    }

    @Override
    public void deleteScheduledTrip(Long id) {
        scheduledTripRepository.deleteById(id);
    }

    @Override
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

    @Override
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

    @Override
    public List<ScheduledTrip> searchScheduledTrips(String scheduledTripId, Direction direction) {
        Long id = null;
        try {
            if (scheduledTripId != null && !scheduledTripId.isEmpty()) {
                id = Long.parseLong(scheduledTripId);
            }
        } catch (NumberFormatException e) {

            // If parsing fails, return empty list
            return Collections.emptyList();
        }
        Specification<ScheduledTrip> spec = Specification.allOf(
                ScheduledTripSpecification.hasScheduledTripId(id),
                ScheduledTripSpecification.hasDirection(direction));

        return scheduledTripRepository.findAll(spec);
    }

}