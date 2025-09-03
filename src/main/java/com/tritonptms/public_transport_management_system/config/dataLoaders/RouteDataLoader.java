package com.tritonptms.public_transport_management_system.config.dataLoaders;

import com.tritonptms.public_transport_management_system.model.Route;
import com.tritonptms.public_transport_management_system.repository.RouteRepository;
import com.tritonptms.public_transport_management_system.repository.ScheduledTripRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
public class RouteDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(RouteDataLoader.class);
    private static final Random random = new Random();

    private final RouteRepository routeRepository;
    private final ScheduledTripRepository scheduledTripRepository;

    public RouteDataLoader(RouteRepository routeRepository, ScheduledTripRepository scheduledTripRepository) {
        this.routeRepository = routeRepository;
        this.scheduledTripRepository = scheduledTripRepository;
    }

    @Transactional
    public void createRouteRecords(boolean shouldRecreate, int numberOfRecords) {
        if (shouldRecreate) {
            logger.info("Recreating route records: Deleting all existing records.");
            scheduledTripRepository.deleteAll();
            routeRepository.deleteAll();
        }

        if (routeRepository.count() == 0) {
            logger.info("Creating {} default route records...", numberOfRecords);
            List<Route> routes = new ArrayList<>();
            // Use a Set to track route numbers for the current batch
            Set<String> generatedRouteNumbers = new HashSet<>();
            for (int i = 0; i < numberOfRecords; i++) {
                routes.add(createUniqueRoute(generatedRouteNumbers));
            }
            routeRepository.saveAll(routes);
            logger.info("Created {} route records.", routes.size());
        } else {
            logger.info(
                    "Route records already exist. Skipping data creation. Set 'shouldRecreate' to true to force recreation.");
        }
    }

    private Route createUniqueRoute(Set<String> generatedRouteNumbers) {
        Route route = new Route();

        // Arrays of relevant Sri Lankan bus route numbers and towns
        String[] routeNumberPrefixes = { "1", "2", "3", "4", "5", "177", "100", "138", "120", "260", "154", "174",
                "190", "255", "332" };
        String[] towns = {
                "Colombo Fort", "Kandy", "Galle", "Anuradhapura", "Jaffna", "Kaduwela",
                "Maharagama", "Piliyandala", "Moratuwa", "Mount Lavinia", "Pettah", "Malabe",
                "Nugegoda", "Kiribathgoda", "Kadawatha", "Horana", "Panadura", "Gampaha",
                "Negombo", "Kalutara", "Kurunegala", "Matara", "Badulla"
        };

        String routeNumber;
        // Keep generating a route number until a unique one is found
        do {
            String prefix = routeNumberPrefixes[random.nextInt(routeNumberPrefixes.length)];
            String suffix = String.valueOf(10 + random.nextInt(90)); // Generate a random two-digit number
            routeNumber = prefix + "/" + suffix;
        } while (generatedRouteNumbers.contains(routeNumber));

        // Add the unique route number to the set
        generatedRouteNumbers.add(routeNumber);

        // Generate origin and destination
        String origin = towns[random.nextInt(towns.length)];
        String destination;
        do {
            destination = towns[random.nextInt(towns.length)];
        } while (origin.equals(destination));

        // Generate major stops
        List<String> majorStops = new ArrayList<>();
        int numberOfStops = 3 + random.nextInt(3);
        for (int i = 0; i < numberOfStops; i++) {
            String stop;
            do {
                stop = towns[random.nextInt(towns.length)];
            } while (majorStops.contains(stop) || stop.equals(origin) || stop.equals(destination));
            majorStops.add(stop);
        }

        route.setRouteNumber(routeNumber);
        route.setOrigin(origin);
        route.setDestination(destination);
        route.setMajorStops(majorStops);

        return route;
    }
}