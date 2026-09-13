package com.tritonptms.ptms.feature.schedule;

import com.tritonptms.ptms.feature.route.RouteMapper;
import com.tritonptms.ptms.feature.schedule.dto.ScheduledTripRequest;
import com.tritonptms.ptms.feature.schedule.dto.ScheduledTripResponse;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTripMapper {

    private final RouteMapper routeMapper;

    public ScheduledTripMapper(RouteMapper routeMapper) {
        this.routeMapper = routeMapper;
    }

    public ScheduledTrip fromRequest(ScheduledTripRequest request) {
        ScheduledTrip trip = new ScheduledTrip();
        apply(trip, request);
        return trip;
    }

    public void apply(ScheduledTrip trip, ScheduledTripRequest request) {
        trip.setDirection(request.direction());
        trip.setExpectedStartTime(request.expectedStartTime());
        trip.setExpectedEndTime(request.expectedEndTime());
    }

    public ScheduledTripResponse toResponse(ScheduledTrip trip) {
        return new ScheduledTripResponse(
                trip.getId(),
                routeMapper.toResponse(trip.getRoute()),
                trip.getDirection(),
                trip.getExpectedStartTime(),
                trip.getExpectedEndTime());
    }
}
