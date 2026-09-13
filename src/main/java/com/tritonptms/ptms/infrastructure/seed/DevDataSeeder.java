package com.tritonptms.ptms.infrastructure.seed;

import com.tritonptms.ptms.feature.assignment.Assignment;
import com.tritonptms.ptms.feature.assignment.AssignmentRepository;
import com.tritonptms.ptms.feature.assignment.AssignmentStatus;
import com.tritonptms.ptms.feature.bus.Bus;
import com.tritonptms.ptms.feature.bus.BusRepository;
import com.tritonptms.ptms.feature.employee.Conductor;
import com.tritonptms.ptms.feature.employee.ConductorRepository;
import com.tritonptms.ptms.feature.employee.Driver;
import com.tritonptms.ptms.feature.employee.DriverRepository;
import com.tritonptms.ptms.feature.route.Route;
import com.tritonptms.ptms.feature.route.RouteRepository;
import com.tritonptms.ptms.feature.schedule.Direction;
import com.tritonptms.ptms.feature.schedule.ScheduledTrip;
import com.tritonptms.ptms.feature.schedule.ScheduledTripRepository;
import com.tritonptms.ptms.feature.user.ERole;
import com.tritonptms.ptms.feature.user.Role;
import com.tritonptms.ptms.feature.user.RoleRepository;
import com.tritonptms.ptms.feature.user.User;
import com.tritonptms.ptms.feature.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Profile("dev")
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
public class DevDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final DriverRepository driverRepository;
    private final ConductorRepository conductorRepository;
    private final ScheduledTripRepository scheduledTripRepository;
    private final AssignmentRepository assignmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataSeeder(UserRepository userRepository,
            RoleRepository roleRepository,
            BusRepository busRepository,
            RouteRepository routeRepository,
            DriverRepository driverRepository,
            ConductorRepository conductorRepository,
            ScheduledTripRepository scheduledTripRepository,
            AssignmentRepository assignmentRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.driverRepository = driverRepository;
        this.conductorRepository = conductorRepository;
        this.scheduledTripRepository = scheduledTripRepository;
        this.assignmentRepository = assignmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedAdminUser();

        Bus bus = seedBus();
        Route route = seedRoute();
        Driver driver = seedDriver();
        Conductor conductor = seedConductor();
        ScheduledTrip trip = seedScheduledTrip(route);
        seedAssignment(trip, bus, driver, conductor);

        log.info("Development sample data is ready.");
    }

    private void seedAdminUser() {
        if (userRepository.existsByUsername("devadmin")) {
            return;
        }

        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN.name())
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN reference data is missing."));

        User admin = new User();
        admin.setUsername("devadmin");
        admin.setPassword(passwordEncoder.encode("Admin123!"));
        admin.setEmail("devadmin@ptms.local");
        admin.setFirstName("Development");
        admin.setLastName("Admin");
        admin.setNic("200000000001");
        admin.setRoles(new HashSet<>(Set.of(adminRole)));
        userRepository.save(admin);
    }

    private Bus seedBus() {
        if (busRepository.count() > 0) {
            return busRepository.findAll().getFirst();
        }

        Bus bus = new Bus();
        bus.setRegistrationNumber("ND-1001");
        bus.setMake("Ashok Leyland");
        bus.setModel("Viking");
        bus.setYearOfManufacture(2022);
        bus.setFuelType(Bus.FuelType.DIESEL);
        bus.setActive(true);
        bus.setSeatingCapacity(45);
        bus.setStandingCapacity(15);
        bus.setNtcPermitNumber(1000000001L);
        bus.setComfortType(Bus.ComfortType.NORMAL);
        bus.setIsA_C(false);
        bus.setServiceType(Bus.ServiceType.PRIVATE_BUS);
        return busRepository.save(bus);
    }

    private Route seedRoute() {
        return routeRepository.findByRouteNumber("177")
                .orElseGet(() -> {
                    Route route = new Route();
                    route.setRouteNumber("177");
                    route.setOrigin("Kaduwela");
                    route.setDestination("Colombo Fort");
                    route.setMajorStops(List.of("Malabe", "Battaramulla", "Borella"));
                    return routeRepository.save(route);
                });
    }

    private Driver seedDriver() {
        if (driverRepository.count() > 0) {
            return driverRepository.findAll().getFirst();
        }

        Driver driver = new Driver();
        driver.setNicNumber("199000000001");
        driver.setFirstName("Nimal");
        driver.setLastName("Perera");
        driver.setDateOfBirth(Date.valueOf(LocalDate.of(1990, 1, 1)));
        driver.setContactNumber("0710000001");
        driver.setEmail("driver@ptms.local");
        driver.setAddress("Colombo");
        driver.setDateJoined(Date.valueOf(LocalDate.of(2024, 1, 1)));
        driver.setIsCurrentEmployee(true);
        driver.setDrivingLicenseNumber("B12345678");
        driver.setLicenseExpirationDate(Date.valueOf(LocalDate.now().plusYears(2)));
        driver.setLicenseClass("D");
        driver.setNtcLicenseNumber("NTCD10001");
        driver.setNtcLicenseExpirationDate(Date.valueOf(LocalDate.now().plusYears(1)));
        driver.setAvailable(true);
        return driverRepository.save(driver);
    }

    private Conductor seedConductor() {
        if (conductorRepository.count() > 0) {
            return conductorRepository.findAll().getFirst();
        }

        Conductor conductor = new Conductor();
        conductor.setNicNumber("199200000002");
        conductor.setFirstName("Sunil");
        conductor.setLastName("Fernando");
        conductor.setDateOfBirth(Date.valueOf(LocalDate.of(1992, 2, 2)));
        conductor.setContactNumber("0710000002");
        conductor.setEmail("conductor@ptms.local");
        conductor.setAddress("Colombo");
        conductor.setDateJoined(Date.valueOf(LocalDate.of(2024, 1, 1)));
        conductor.setIsCurrentEmployee(true);
        conductor.setConductorLicenseNumber("C12345678");
        conductor.setLicenseExpirationDate(Date.valueOf(LocalDate.now().plusYears(2)));
        conductor.setAvailable(true);
        return conductorRepository.save(conductor);
    }

    private ScheduledTrip seedScheduledTrip(Route route) {
        if (scheduledTripRepository.count() > 0) {
            return scheduledTripRepository.findAll().getFirst();
        }

        ScheduledTrip trip = new ScheduledTrip();
        trip.setRoute(route);
        trip.setDirection(Direction.TO);
        trip.setExpectedStartTime(LocalTime.of(6, 30));
        trip.setExpectedEndTime(LocalTime.of(8, 0));
        return scheduledTripRepository.save(trip);
    }

    private void seedAssignment(ScheduledTrip trip, Bus bus, Driver driver, Conductor conductor) {
        if (assignmentRepository.count() > 0) {
            return;
        }

        Assignment assignment = new Assignment();
        assignment.setScheduledTrip(trip);
        assignment.setBus(bus);
        assignment.setDriver(driver);
        assignment.setConductor(conductor);
        assignment.setDate(LocalDate.now());
        assignment.setStatus(AssignmentStatus.SCHEDULED);
        assignmentRepository.save(assignment);
    }
}
