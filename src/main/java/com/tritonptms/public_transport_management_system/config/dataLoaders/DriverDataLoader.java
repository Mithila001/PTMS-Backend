// F:\OnGoinProject\Transport Management System\public-transport-management-system\src\main\java\com\tritonptms\public_transport_management_system\config\dataLoaders\DriverDataLoader.java

package com.tritonptms.public_transport_management_system.config.dataLoaders;

import com.tritonptms.public_transport_management_system.model.Driver;
import com.tritonptms.public_transport_management_system.repository.DriverRepository;
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
public class DriverDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DriverDataLoader.class);
    private static final Random random = new Random();

    private final DriverRepository driverRepository;

    public DriverDataLoader(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Transactional
    public void createDriverRecords(boolean shouldRecreate, int numberOfRecords) {
        if (shouldRecreate) {
            logger.info("Recreating driver records: Deleting all existing records.");
            driverRepository.deleteAll();
        }

        if (driverRepository.count() == 0) {
            logger.info("Creating {} default driver records...", numberOfRecords);
            List<Driver> drivers = new ArrayList<>();
            for (int i = 0; i < numberOfRecords; i++) {
                drivers.add(createRandomDriver(i)); // Pass the loop index to ensure a unique email
            }
            driverRepository.saveAll(drivers);
            logger.info("Created {} driver records.", drivers.size());
        } else {
            logger.info(
                    "Driver records already exist. Skipping data creation. Set 'shouldRecreate' to true to force recreation.");
        }
    }

    private Driver createRandomDriver(int index) {
        Driver driver = new Driver();

        // Employee fields
        String[] firstNames = { "Nimal", "Kamal", "Sunil", "Amal", "Wasantha", "Samantha", "Chathura" };
        String[] lastNames = { "Perera", "Silva", "Bandara", "Fernando", "Jayasinghe", "Dissanayake" };
        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];

        driver.setNicNumber(generateRandomNic());
        driver.setFirstName(firstName);
        driver.setLastName(lastName);
        driver.setDateOfBirth(generateRandomDateOfBirth());
        driver.setContactNumber("07" + String.format("%08d", random.nextInt(100000000)));
        // Make the email unique using the provided index
        driver.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "." + index + "@tritonbus.lk");
        driver.setAddress(generateRandomAddress());
        driver.setDateJoined(generateRandomDateJoined());
        driver.setIsCurrentEmployee(true);

        // Driver-specific fields
        driver.setDrivingLicenseNumber("D" + String.format("%08d", random.nextInt(100000000)));
        driver.setLicenseExpirationDate(generateFutureDate());
        driver.setLicenseClass("Heavy Vehicle");
        driver.setNtcLicenseNumber("NTC" + String.format("%06d", random.nextInt(1000000)));
        driver.setNtcLicenseExpirationDate(generateFutureDate());
        driver.setAvailable(random.nextBoolean());

        return driver;
    }

    private String generateRandomNic() {
        // Old NIC format (9 digits + 'V') or New NIC format (12 digits)
        boolean isOldNic = random.nextBoolean();
        if (isOldNic) {
            return String.format("%09d", random.nextInt(1000000000)) + (random.nextBoolean() ? "V" : "v");
        } else {
            return String.format("%012d", ThreadLocalRandom.current().nextLong(100000000000L, 999999999999L));
        }
    }

    private Date generateRandomDateOfBirth() {
        long aDay = TimeUnit.DAYS.toMillis(1);
        long start = new Date(System.currentTimeMillis() - aDay * 365 * 60).getTime(); // max 60 years ago
        long end = new Date(System.currentTimeMillis() - aDay * 365 * 25).getTime(); // min 25 years ago
        long dateOfBirthMillis = ThreadLocalRandom.current().nextLong(start, end);
        return new Date(dateOfBirthMillis);
    }

    private Date generateRandomDateJoined() {
        long aDay = TimeUnit.DAYS.toMillis(1);
        long start = new Date(System.currentTimeMillis() - aDay * 365 * 10).getTime(); // max 10 years ago
        long end = System.currentTimeMillis();
        long dateJoinedMillis = ThreadLocalRandom.current().nextLong(start, end);
        return new Date(dateJoinedMillis);
    }

    private Date generateFutureDate() {
        long aDay = TimeUnit.DAYS.toMillis(1);
        long start = System.currentTimeMillis();
        long end = start + aDay * 365 * 5; // up to 5 years in the future
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