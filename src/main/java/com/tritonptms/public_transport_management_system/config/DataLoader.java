// F:\OnGoinProject\Transport Management System\public-transport-management-system\src\main\java\com\tritonptms\public_transport_management_system\config\DataLoader.java

package com.tritonptms.public_transport_management_system.config;

import com.tritonptms.public_transport_management_system.config.dataLoaders.AssignmentDataLoader;
import com.tritonptms.public_transport_management_system.config.dataLoaders.BusDataLoader;
import com.tritonptms.public_transport_management_system.config.dataLoaders.ConductorDataLoader;
import com.tritonptms.public_transport_management_system.config.dataLoaders.DriverDataLoader;
import com.tritonptms.public_transport_management_system.config.dataLoaders.RouteDataLoader;
import com.tritonptms.public_transport_management_system.config.dataLoaders.ScheduledTripDataLoader;
import com.tritonptms.public_transport_management_system.config.dataLoaders.UserDataLoader;
import com.tritonptms.public_transport_management_system.model.Role;
import com.tritonptms.public_transport_management_system.model.User;
import com.tritonptms.public_transport_management_system.model.enums.users.ERole;
import com.tritonptms.public_transport_management_system.repository.RoleRepository;
import com.tritonptms.public_transport_management_system.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.Console;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Profile("dev")
@Component
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final BusDataLoader busDataLoader;
    private final RouteDataLoader routeDataLoader;
    private final ScheduledTripDataLoader scheduledTripDataLoader;
    private final DriverDataLoader driverDataLoader;
    private final ConductorDataLoader conductorDataLoader;
    private final AssignmentDataLoader assignmentDataLoader;
    private final UserDataLoader userDataLoader;

    @Value("${DEV_DATA_LOADER_ENABLED:false}")
    private boolean isDataLoadingEnabled;

    // Use a single, clean constructor for dependency injection
    public DataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
            BusDataLoader busDataLoader, RouteDataLoader routeDataLoader,
            ScheduledTripDataLoader scheduledTripDataLoader, DriverDataLoader driverDataLoader,
            ConductorDataLoader conductorDataLoader, AssignmentDataLoader assignmentDataLoader,
            UserDataLoader userDataLoader) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.busDataLoader = busDataLoader;
        this.routeDataLoader = routeDataLoader;
        this.scheduledTripDataLoader = scheduledTripDataLoader;
        this.driverDataLoader = driverDataLoader;
        this.conductorDataLoader = conductorDataLoader;
        this.assignmentDataLoader = assignmentDataLoader;
        this.userDataLoader = userDataLoader;
    }

    @PostConstruct
    public void init() {
        logger.info("Initializing default roles and users...");

        // Centralized configuration for data loaders
        boolean shouldRecreateData = isDataLoadingEnabled;

        // Initialize new Users and Roles via UserDataLoader
        userDataLoader.initializeDefaultUsersAndRoles(true);

        busDataLoader.createBusRecords(shouldRecreateData, 200);
        routeDataLoader.createRouteRecords(shouldRecreateData, 20);
        scheduledTripDataLoader.createScheduledTripRecords(shouldRecreateData, 20);
        driverDataLoader.createDriverRecords(shouldRecreateData, 20);
        conductorDataLoader.createConductorRecords(shouldRecreateData, 20);
        assignmentDataLoader.createAssignmentRecords(shouldRecreateData, 30);
    }

}