// F:\OnGoinProject\Transport Management System\public-transport-management-system\src\main\java\com\tritonptms\public_transport_management_system\config\DataLoader.java

package com.tritonptms.public_transport_management_system.config;

import com.tritonptms.public_transport_management_system.config.dataLoaders.AssignmentDataLoader;
import com.tritonptms.public_transport_management_system.config.dataLoaders.BusDataLoader;
import com.tritonptms.public_transport_management_system.config.dataLoaders.ConductorDataLoader;
import com.tritonptms.public_transport_management_system.config.dataLoaders.DriverDataLoader;
import com.tritonptms.public_transport_management_system.config.dataLoaders.RouteDataLoader;
import com.tritonptms.public_transport_management_system.config.dataLoaders.ScheduledTripDataLoader;
import com.tritonptms.public_transport_management_system.model.Role;
import com.tritonptms.public_transport_management_system.model.User;
import com.tritonptms.public_transport_management_system.model.enums.users.ERole;
import com.tritonptms.public_transport_management_system.repository.RoleRepository;
import com.tritonptms.public_transport_management_system.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    // Use a single, clean constructor for dependency injection
    public DataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
            BusDataLoader busDataLoader, RouteDataLoader routeDataLoader,
            ScheduledTripDataLoader scheduledTripDataLoader, DriverDataLoader driverDataLoader,
            ConductorDataLoader conductorDataLoader, AssignmentDataLoader assignmentDataLoader) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.busDataLoader = busDataLoader;
        this.routeDataLoader = routeDataLoader;
        this.scheduledTripDataLoader = scheduledTripDataLoader;
        this.driverDataLoader = driverDataLoader;
        this.conductorDataLoader = conductorDataLoader;
        this.assignmentDataLoader = assignmentDataLoader;
    }

    @PostConstruct
    public void init() {
        logger.info("Initializing default roles and users...");

        Role adminRole = findOrCreateRole(ERole.ROLE_ADMIN.name());
        Role operationsManagerRole = findOrCreateRole(ERole.ROLE_OPERATIONS_MANAGER.name());

        if (!userRepository.existsByUsername("admin")) {
            User adminUser = new User("admin", passwordEncoder.encode("adminpass"));
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminUser.setRoles(adminRoles);
            userRepository.save(adminUser);
            logger.info("Created default ADMIN user: admin");
        } else {
            logger.info("Default ADMIN user already exists.");
        }

        if (!userRepository.existsByUsername("opsmanager")) {
            User opsManagerUser = new User("opsmanager", passwordEncoder.encode("opspass"));
            Set<Role> opsManagerRoles = new HashSet<>();
            opsManagerRoles.add(operationsManagerRole);
            opsManagerUser.setRoles(opsManagerRoles);
            userRepository.save(opsManagerUser);
            logger.info("Created default OPERATIONS_MANAGER user: opsmanager");
        } else {
            logger.info("Default OPERATIONS_MANAGER user already exists.");
        }

        logger.info("Default roles and users initialization complete.");

        userRepository.findByUsername("admin").ifPresent(user -> {
            String rawPassword = "adminpass";
            if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(rawPassword));
                userRepository.save(user);
                logger.warn("Corrected stale password for user: 'admin'.");
            }
        });

        // Centralized configuration for data loaders
        boolean shouldRecreateData = true;

        busDataLoader.createBusRecords(shouldRecreateData, 10);
        routeDataLoader.createRouteRecords(shouldRecreateData, 20);
        scheduledTripDataLoader.createScheduledTripRecords(shouldRecreateData, 20);
        driverDataLoader.createDriverRecords(shouldRecreateData, 20);
        conductorDataLoader.createConductorRecords(shouldRecreateData, 20);
        assignmentDataLoader.createAssignmentRecords(shouldRecreateData, 30);
    }

    private Role findOrCreateRole(String roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            Role newRole = new Role(roleName);
            roleRepository.save(newRole);
            logger.info("Created new role: {}", roleName);
            return newRole;
        }
        logger.info("Role already exists: {}", roleName);
        return role.get();
    }
}