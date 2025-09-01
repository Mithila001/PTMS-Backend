package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.ScheduledTrip;
import com.tritonptms.public_transport_management_system.model.enums.route.Direction;
import com.tritonptms.public_transport_management_system.dto.ScheduledTripDto;
import java.util.List;
import java.util.Optional;

public interface ScheduledTripService {
    List<ScheduledTripDto> getAllScheduledTrips();

    Optional<ScheduledTripDto> getScheduledTripById(Long id);

    ScheduledTrip saveScheduledTrip(ScheduledTripDto scheduledTripDto);

    void deleteScheduledTrip(Long id);

    ScheduledTripDto convertToDto(ScheduledTrip scheduledTrip);

    ScheduledTrip convertToEntity(ScheduledTripDto scheduledTripDto);

    List<ScheduledTrip> searchScheduledTrips(String routeNumber, Direction direction);
}