package com.tritonptms.public_transport_management_system.config.dataLoaders;

import com.tritonptms.public_transport_management_system.model.Role;
import com.tritonptms.public_transport_management_system.model.User;
import com.tritonptms.public_transport_management_system.model.enums.users.ERole;
import com.tritonptms.public_transport_management_system.repository.RoleRepository;
import com.tritonptms.public_transport_management_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // Import for transactional operations

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(UserDataLoader.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // A record (or simple immutable class) to hold the required data for initial
    // users
    private record InitialUserData(
            String username,
            String rawPassword,
            String email,
            String firstName,
            String lastName,
            String nic,
            ERole role) {
    }

    // Define the list of initial users using the strongly-typed record
    private static final List<InitialUserData> DEFAULT_USERS = Arrays.asList(
            new InitialUserData("admin", "adminpass", "admin@example.com", "Admin", "User", "000100000V",
                    ERole.ROLE_ADMIN),
            new InitialUserData("ops", "opspass", "ops@example.com", "Operations", "Manager", "020000001V",
                    ERole.ROLE_OPERATIONS_MANAGER),
            new InitialUserData("testuser", "testpass", "testuser@example.com", "Test", "User", "000000002V",
                    ERole.ROLE_USER));

    public UserDataLoader(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Initializes default roles and administrative users.
     * 
     * @param shouldRewriteUserRecords If true, all existing users and roles are
     *                                 deleted first.
     */
    @Transactional // Ensures deletion and creation happen in one atomic unit
    public void initializeDefaultUsersAndRoles(boolean shouldRewriteUserRecords) {
        logger.info("Initializing default roles and users. Rewrite enabled: {}", shouldRewriteUserRecords);

        if (shouldRewriteUserRecords) {
            deleteAllUserAndRoleData();
        }

        // 1. Create/Find all required roles first
        Set<String> requiredRoleNames = DEFAULT_USERS.stream()
                .map(data -> data.role().name())
                .collect(Collectors.toSet());

        // This method will only create the roles if they don't exist
        requiredRoleNames.forEach(this::findOrCreateRole);

        // 2. Create Default Users using the iterative method
        createDefaultUsersFromList();

        logger.info("Default roles and users initialization complete.");
    }

    private void deleteAllUserAndRoleData() {
        logger.warn("REWRITE MODE: Deleting all existing user data!");
        userRepository.deleteAll();

        // We delete roles last, as they might have foreign key constraints
        // with the User table.
        logger.warn("REWRITE MODE: Deleting all existing role data!");
        roleRepository.deleteAll();
    }

    private Role findOrCreateRole(String roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            Role newRole = new Role(roleName);
            roleRepository.save(newRole);
            logger.info("Created new role: {}", roleName);
            return newRole;
        }
        // If shouldRewriteUserRecords is true, the role would have been deleted, so it
        // will be
        // recreated.
        // If shouldRewriteUserRecords is false, the existing role is returned.
        logger.debug("Role already exists: {}", roleName);
        return role.get();
    }

    /**
     * Iterates over the list of defined users and creates them if they don't exist.
     */
    private void createDefaultUsersFromList() {
        for (InitialUserData userData : DEFAULT_USERS) {
            // Note: If shouldRewriteUserRecords was true, this check is guaranteed to be
            // false on the
            // first run.
            if (!userRepository.existsByUsername(userData.username())) {

                // Get the role object from the database/context
                Role role = roleRepository.findByName(userData.role().name())
                        .orElseThrow(() -> new RuntimeException(
                                "Role not found during user creation: " + userData.role().name()));

                User newUser = new User(
                        userData.email(),
                        userData.firstName(),
                        userData.lastName(),
                        userData.nic());

                newUser.setUsername(userData.username());
                // Encrypt the password from the data list
                newUser.setPassword(passwordEncoder.encode(userData.rawPassword()));

                Set<Role> userRoles = new HashSet<>();
                userRoles.add(role);
                newUser.setRoles(userRoles);

                userRepository.save(newUser);
                logger.info("Created default {} user: {}", userData.role().name(), userData.username());
            } else {
                logger.info("Default {} user already exists: {}", userData.role().name(), userData.username());
            }
        }
    }
}