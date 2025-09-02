// F:\OnGoinProject\Transport Management System\public-transport-management-system\src\main\java\com\tritonptms\public_transport_management_system\config\BusDataLoader.java

package com.tritonptms.public_transport_management_system.config.dataLoaders;

import com.tritonptms.public_transport_management_system.model.Bus;
import com.tritonptms.public_transport_management_system.model.Bus.ComfortType;
import com.tritonptms.public_transport_management_system.model.Bus.ServiceType;
import com.tritonptms.public_transport_management_system.model.Vehicle.FuelType;
import com.tritonptms.public_transport_management_system.repository.AssignmentRepository;
import com.tritonptms.public_transport_management_system.repository.BusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class BusDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(BusDataLoader.class);
    private static final Random random = new Random();

    private final BusRepository busRepository;
    private final AssignmentRepository assignmentRepository;

    public BusDataLoader(BusRepository busRepository, AssignmentRepository assignmentRepository) {
        this.busRepository = busRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional
    public void createBusRecords(boolean shouldRecreate, int numberOfRecords) {
        if (shouldRecreate) {
            logger.info("Recreating bus records: Deleting all existing records.");
            assignmentRepository.deleteAll();
            busRepository.deleteAll();
        }

        if (busRepository.count() == 0) {
            logger.info("Creating {} default bus records...", numberOfRecords);
            List<Bus> buses = new ArrayList<>();
            for (int i = 0; i < numberOfRecords; i++) {
                buses.add(createRandomBus());
            }
            busRepository.saveAll(buses);
            logger.info("Created {} bus records.", buses.size());
        } else {
            logger.info(
                    "Bus records already exist. Skipping data creation. Set 'shouldRecreate' to true to force recreation.");
        }
    }

    private Bus createRandomBus() {
        Bus bus = new Bus();

        String[] makes = { "Lanka Ashok Leyland", "Volvo", "Tata Motors", "Isuzu" };
        String[] models = { "Comet", "Viking", "B8R", "B11R", "Starbus" };
        FuelType[] fuelTypes = FuelType.values();
        ComfortType[] comfortTypes = ComfortType.values();
        ServiceType[] serviceTypes = ServiceType.values();

        bus.setRegistrationNumber(generateRandomRegistrationNumber());
        bus.setMake(makes[random.nextInt(makes.length)]);
        bus.setModel(models[random.nextInt(models.length)]);
        bus.setYearOfManufacture(2015 + random.nextInt(10));
        bus.setFuelType(fuelTypes[random.nextInt(fuelTypes.length)]);
        bus.setActive(true);
        bus.setSeatingCapacity(40 + random.nextInt(20));
        bus.setStandingCapacity(10 + random.nextInt(10));
        long ntcPermitBase = 1_000_000_000L;
        bus.setNtcPermitNumber(ntcPermitBase + random.nextInt(900000000));
        bus.setComfortType(comfortTypes[random.nextInt(comfortTypes.length)]);
        bus.setServiceType(serviceTypes[random.nextInt(serviceTypes.length)]);

        bus.setIsA_C(random.nextBoolean());

        return bus;
    }

    private String generateRandomRegistrationNumber() {
        String[] prefixes = { "NA", "NB", "NC", "ND", "NE" };
        String prefix = prefixes[random.nextInt(prefixes.length)];
        int number = 1000 + random.nextInt(9000);
        return prefix + "-" + number;
    }
}