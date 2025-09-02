// F:\OnGoinProject\Transport Management System\public-transport-management-system\src\main\java\com\tritonptms\public_transport_management_system\config\RouteDataLoader.java

package com.tritonptms.public_transport_management_system.config.dataLoaders;

import com.tritonptms.public_transport_management_system.model.Route;
import com.tritonptms.public_transport_management_system.repository.RouteRepository;
import com.tritonptms.public_transport_management_system.repository.ScheduledTripRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            for (int i = 0; i < numberOfRecords; i++) {
                routes.add(createRandomRoute());
            }
            routeRepository.saveAll(routes);
            logger.info("Created {} route records.", routes.size());
        } else {
            logger.info(
                    "Route records already exist. Skipping data creation. Set 'shouldRecreate' to true to force recreation.");
        }
    }

    private Route createRandomRoute() {
        Route route = new Route();

        String[] routeNumbers = { "1", "2", "3", "4", "5", "177", "100", "138", "120", "260" };
        String[] towns = {
                "Colombo Fort", "Kandy", "Galle", "Anuradhapura", "Jaffna", "Kaduwela",
                "Maharagama", "Piliyandala", "Moratuwa", "Mount Lavinia", "Pettah", "Malabe",
                "Nugegoda", "Kiribathgoda", "Kadawatha", "Horana", "Panadura", "Gampaha",
                "Negombo", "Kalutara", "Kurunegala", "Matara", "Badulla"
        };

        String origin = towns[random.nextInt(towns.length)];
        String destination;
        do {
            destination = towns[random.nextInt(towns.length)];
        } while (origin.equals(destination));

        List<String> majorStops = new ArrayList<>();
        int numberOfStops = 3 + random.nextInt(3);
        for (int i = 0; i < numberOfStops; i++) {
            String stop;
            do {
                stop = towns[random.nextInt(towns.length)];
            } while (majorStops.contains(stop) || stop.equals(origin) || stop.equals(destination));
            majorStops.add(stop);
        }

        route.setRouteNumber(routeNumbers[random.nextInt(routeNumbers.length)] + "/" + (10 + random.nextInt(90)));
        route.setOrigin(origin);
        route.setDestination(destination);
        route.setMajorStops(majorStops);

        return route;
    }
}