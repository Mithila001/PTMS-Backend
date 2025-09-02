// F:\OnGoinProject\Transport Management System\public-transport-management-system\src\main\java\com\tritonptms\public_transport_management_system\config\dataLoaders\ConductorDataLoader.java

package com.tritonptms.public_transport_management_system.config.dataLoaders;

import com.tritonptms.public_transport_management_system.model.Conductor;
import com.tritonptms.public_transport_management_system.repository.ConductorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
public class ConductorDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConductorDataLoader.class);
    private static final Random random = new Random();

    private final ConductorRepository conductorRepository;

    public ConductorDataLoader(ConductorRepository conductorRepository) {
        this.conductorRepository = conductorRepository;
    }

    @Transactional
    public void createConductorRecords(boolean shouldRecreate, int numberOfRecords) {
        if (shouldRecreate) {
            logger.info("Recreating conductor records: Deleting all existing records.");
            conductorRepository.deleteAll();
        }

        if (conductorRepository.count() == 0) {
            logger.info("Creating {} default conductor records...", numberOfRecords);
            List<Conductor> conductors = new ArrayList<>();
            for (int i = 0; i < numberOfRecords; i++) {
                conductors.add(createRandomConductor(i));
            }
            conductorRepository.saveAll(conductors);
            logger.info("Created {} conductor records.", conductors.size());
        } else {
            logger.info(
                    "Conductor records already exist. Skipping data creation. Set 'shouldRecreate' to true to force recreation.");
        }
    }

    private Conductor createRandomConductor(int index) {
        Conductor conductor = new Conductor();

        // Employee fields
        String[] firstNames = { "Sunil", "Nimal", "Kamal", "Chathura", "Sanduni" };
        String[] lastNames = { "Perera", "Silva", "Bandara", "Fernando", "Jayasinghe" };
        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];

        conductor.setNicNumber(generateRandomNic());
        conductor.setFirstName(firstName);
        conductor.setLastName(lastName);
        conductor.setDateOfBirth(generateRandomDateOfBirth());
        conductor.setContactNumber("07" + String.format("%08d", random.nextInt(100000000)));
        conductor.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "." + index + "@tritonbus.lk");
        conductor.setAddress(generateRandomAddress());
        conductor.setDateJoined(generateRandomDateJoined());
        conductor.setIsCurrentEmployee(true);

        // Conductor-specific fields
        conductor.setConductorLicenseNumber("CL" + String.format("%08d", random.nextInt(100000000)));
        conductor.setLicenseExpirationDate(generateFutureDate());
        conductor.setAvailable(random.nextBoolean());

        return conductor;
    }

    private String generateRandomNic() {
        boolean isOldNic = random.nextBoolean();
        if (isOldNic) {
            return String.format("%09d", random.nextInt(1000000000)) + (random.nextBoolean() ? "V" : "v");
        } else {
            return String.format("%012d", ThreadLocalRandom.current().nextLong(100000000000L, 999999999999L));
        }
    }

    private Date generateRandomDateOfBirth() {
        long aDay = TimeUnit.DAYS.toMillis(1);
        long start = new Date(System.currentTimeMillis() - aDay * 365 * 60).getTime();
        long end = new Date(System.currentTimeMillis() - aDay * 365 * 25).getTime();
        long dateOfBirthMillis = ThreadLocalRandom.current().nextLong(start, end);
        return new Date(dateOfBirthMillis);
    }

    private Date generateRandomDateJoined() {
        long aDay = TimeUnit.DAYS.toMillis(1);
        long start = new Date(System.currentTimeMillis() - aDay * 365 * 10).getTime();
        long end = System.currentTimeMillis();
        long dateJoinedMillis = ThreadLocalRandom.current().nextLong(start, end);
        return new Date(dateJoinedMillis);
    }

    private Date generateFutureDate() {
        long aDay = TimeUnit.DAYS.toMillis(1);
        long start = System.currentTimeMillis();
        long end = start + aDay * 365 * 5;
        long futureDateMillis = ThreadLocalRandom.current().nextLong(start, end);
        return new Date(futureDateMillis);
    }

    private String generateRandomAddress() {
        String[] streets = { "Main Street", "Temple Road", "Galle Road", "High Level Road", "Kandy Road" };
        String[] towns = { "Colombo", "Kandy", "Galle", "Negombo", "Jaffna" };
        return "No. " + (random.nextInt(100) + 1) + ", " +
                streets[random.nextInt(streets.length)] + ", " +
                towns[random.nextInt(towns.length)];
    }
}