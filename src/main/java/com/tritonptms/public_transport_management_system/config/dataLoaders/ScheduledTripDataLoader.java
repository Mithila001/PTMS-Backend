package com.tritonptms.public_transport_management_system.config.dataLoaders;

import com.tritonptms.public_transport_management_system.model.Route;
import com.tritonptms.public_transport_management_system.model.ScheduledTrip;
import com.tritonptms.public_transport_management_system.model.enums.route.Direction;
import com.tritonptms.public_transport_management_system.repository.RouteRepository;
import com.tritonptms.public_transport_management_system.repository.ScheduledTripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ScheduledTripDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTripDataLoader.class);
    private static final Random random = new Random();

    private final RouteRepository routeRepository;
    private final ScheduledTripRepository scheduledTripRepository;

    public ScheduledTripDataLoader(RouteRepository routeRepository, ScheduledTripRepository scheduledTripRepository) {
        this.routeRepository = routeRepository;
        this.scheduledTripRepository = scheduledTripRepository;
    }

    @Transactional
    public void createScheduledTripRecords(boolean shouldRecreate, int numberOfRecords) {
        if (shouldRecreate) {
            logger.info("Recreating scheduled trip records: Deleting all existing records.");
            scheduledTripRepository.deleteAll();
        }

        if (scheduledTripRepository.count() == 0) {
            logger.info("Creating {} default scheduled trip records...", numberOfRecords);
            List<Route> routes = routeRepository.findAll();
            if (routes.isEmpty()) {
                logger.warn("No routes found. Skipping scheduled trip creation.");
                return;
            }

            List<ScheduledTrip> scheduledTrips = new ArrayList<>();
            for (int i = 0; i < numberOfRecords; i++) {
                scheduledTrips.add(createRandomScheduledTrip(routes));
            }
            scheduledTripRepository.saveAll(scheduledTrips);
            logger.info("Created {} scheduled trip records.", scheduledTrips.size());
        } else {
            logger.info(
                    "Scheduled trip records already exist. Skipping data creation. Set 'shouldRecreate' to true to force recreation.");
        }
    }

    private ScheduledTrip createRandomScheduledTrip(List<Route> routes) {
        ScheduledTrip scheduledTrip = new ScheduledTrip();

        // Pick a random route from the list
        Route randomRoute = routes.get(random.nextInt(routes.size()));

        // Determine a random direction based on the route's origin and destination
        Direction direction = Direction.values()[random.nextInt(Direction.values().length)];

        // Generate a random start time and duration for a trip between 1 and 3 hours
        LocalTime startTime = LocalTime.of(ThreadLocalRandom.current().nextInt(5, 23), 0); // 5 AM to 11 PM
        LocalTime endTime = startTime.plusHours(1 + random.nextInt(3));

        scheduledTrip.setRoute(randomRoute);
        scheduledTrip.setDirection(direction);
        scheduledTrip.setExpectedStartTime(startTime);
        scheduledTrip.setExpectedEndTime(endTime);

        return scheduledTrip;
    }
}
