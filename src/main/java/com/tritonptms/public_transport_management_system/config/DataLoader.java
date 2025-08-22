// F:\OnGoinProject\Transport Management System\public-transport-management-system\src\main\java\com\tritonptms\public_transport_management_system\config\DataLoader.java

package com.tritonptms.public_transport_management_system.config;

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

    public DataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
        
        // TEMPORARY: Ensure the password for a known user is set correctly
        updateKnownUserPassword(); // <-- ADD THIS LINE

        logger.info("Default roles and users initialization complete.");
    }
    
    // TEMPORARY METHOD: Update a specific user's password directly after creation
    private void updateKnownUserPassword() {
        Optional<User> userOptional = userRepository.findByUsername("admin");
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // This is a known password that should work
            user.setPassword(passwordEncoder.encode("adminpass")); 
            userRepository.save(user);
            logger.info("Forced password update for user 'admin' to 'adminpass'.");
        }
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