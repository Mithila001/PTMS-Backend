// F:\OnGoinProject\Transport Management System\public-transport-management-system\src\main\java\com\tritonptms\public_transport_management_system\config\dataLoaders\AssignmentDataLoader.java

package com.tritonptms.public_transport_management_system.config.dataLoaders;

import com.tritonptms.public_transport_management_system.model.*;
import com.tritonptms.public_transport_management_system.model.enums.assignment.AssignmentStatus;
import com.tritonptms.public_transport_management_system.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class AssignmentDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentDataLoader.class);
    private static final Random random = new Random();

    private final AssignmentRepository assignmentRepository;
    private final ScheduledTripRepository scheduledTripRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final ConductorRepository conductorRepository;

    public AssignmentDataLoader(AssignmentRepository assignmentRepository,
            ScheduledTripRepository scheduledTripRepository,
            BusRepository busRepository, DriverRepository driverRepository, ConductorRepository conductorRepository) {
        this.assignmentRepository = assignmentRepository;
        this.scheduledTripRepository = scheduledTripRepository;
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.conductorRepository = conductorRepository;
    }

    @Transactional
    public void createAssignmentRecords(boolean shouldRecreate, int numberOfRecords) {
        if (shouldRecreate) {
            logger.info("Recreating assignment records: Deleting all existing records.");
            assignmentRepository.deleteAll();
        }

        if (assignmentRepository.count() == 0) {
            logger.info("Creating {} default assignment records...", numberOfRecords);

            List<ScheduledTrip> scheduledTrips = scheduledTripRepository.findAll();
            List<Bus> buses = busRepository.findAll();
            List<Driver> drivers = driverRepository.findAll();
            List<Conductor> conductors = conductorRepository.findAll();

            if (scheduledTrips.isEmpty() || buses.isEmpty() || drivers.isEmpty() || conductors.isEmpty()) {
                logger.warn("Not enough data to create assignments. Skipping assignment creation.");
                logger.info("Counts: ScheduledTrips={}, Buses={}, Drivers={}, Conductors={}",
                        scheduledTrips.size(), buses.size(), drivers.size(), conductors.size());
                return;
            }

            List<Assignment> assignments = new ArrayList<>();
            for (int i = 0; i < numberOfRecords; i++) {
                assignments.add(createRandomAssignment(scheduledTrips, buses, drivers, conductors));
            }
            assignmentRepository.saveAll(assignments);
            logger.info("Created {} assignment records.", assignments.size());
        } else {
            logger.info(
                    "Assignment records already exist. Skipping data creation. Set 'shouldRecreate' to true to force recreation.");
        }
    }

    private Assignment createRandomAssignment(List<ScheduledTrip> scheduledTrips, List<Bus> buses, List<Driver> drivers,
            List<Conductor> conductors) {
        Assignment assignment = new Assignment();

        // Randomly assign foreign key entities
        assignment.setScheduledTrip(scheduledTrips.get(random.nextInt(scheduledTrips.size())));
        assignment.setBus(buses.get(random.nextInt(buses.size())));
        assignment.setDriver(drivers.get(random.nextInt(drivers.size())));
        assignment.setConductor(conductors.get(random.nextInt(conductors.size())));

        // Set date
        LocalDate date = LocalDate.now().plusDays(random.nextInt(7) - 3); // A few days around the current date
        assignment.setDate(date);

        // Set status and actual times based on a random outcome
        if (random.nextBoolean()) {
            assignment.setStatus(AssignmentStatus.COMPLETED);
            // Example of setting actual times slightly different from scheduled times
            assignment.setActualStartTime(LocalDateTime.of(date, assignment.getScheduledTrip().getExpectedStartTime())
                    .plusMinutes(random.nextInt(15)));
            assignment.setActualEndTime(LocalDateTime.of(date, assignment.getScheduledTrip().getExpectedEndTime())
                    .plusMinutes(random.nextInt(15)));
        } else {
            assignment.setStatus(AssignmentStatus.SCHEDULED);
        }

        return assignment;
    }
}