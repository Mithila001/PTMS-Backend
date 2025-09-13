package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.User;
import com.tritonptms.public_transport_management_system.model.Role;
import com.tritonptms.public_transport_management_system.model.enums.users.ERole;
import com.tritonptms.public_transport_management_system.repository.RoleRepository;
import com.tritonptms.public_transport_management_system.repository.UserRepository;
import com.tritonptms.public_transport_management_system.dto.auth.RegisterRequestDto;
import com.tritonptms.public_transport_management_system.dto.auth.RegisterResponseDto;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterResponseDto registerNewUser(RegisterRequestDto registerRequest) {

        // --- Security Check: Only ADMIN can register users ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ERole.ROLE_ADMIN.name()));

        if (!isAdmin) {
            throw new AccessDeniedException("Only ADMIN users are allowed to register new employees.");
        }
        // --- End of Security Check ---

        // Generate username and temporary password
        String generatedUsername = "bms" + registerRequest.getFirstName().toLowerCase()
                + registerRequest.getLastName().toLowerCase();
        String generatedPassword = "2196697";

        // Check if username or email already exists
        if (userRepository.existsByUsername(generatedUsername)) {
            throw new RuntimeException("Error: Username '" + generatedUsername + "' is already taken.");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(
                registerRequest.getEmail(),
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getNic());

        user.setUsername(generatedUsername);
        user.setPassword(passwordEncoder.encode(generatedPassword));

        Set<Role> roles = new HashSet<>();
        String requestedRoleName = registerRequest.getRole().toUpperCase();

        // Find the requested role from the database
        Role assignedRole = roleRepository.findByName(requestedRoleName)
                .orElseThrow(() -> new RuntimeException("Error: Role '" + requestedRoleName + "' not found."));

        roles.add(assignedRole);
        user.setRoles(roles);
        userRepository.save(user);

        // Return the DTO with the generated username and temporary password
        return new RegisterResponseDto(
                generatedUsername,
                generatedPassword,
                "User registered successfully! Please use the provided credentials to log in.");
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}