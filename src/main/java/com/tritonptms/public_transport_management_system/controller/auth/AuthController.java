// F:\OnGoinProject\Transport Management System\public-transport-management-system\src\main\java\com\tritonptms\public_transport_management_system\controller\auth\AuthController.java

package com.tritonptms.public_transport_management_system.controller.auth;

import com.tritonptms.public_transport_management_system.model.Role;
import com.tritonptms.public_transport_management_system.model.User;
import com.tritonptms.public_transport_management_system.model.enums.users.ERole;
import com.tritonptms.public_transport_management_system.repository.RoleRepository;
import com.tritonptms.public_transport_management_system.repository.UserRepository;
import com.tritonptms.public_transport_management_system.dto.auth.LoginRequest;
import com.tritonptms.public_transport_management_system.dto.auth.UserInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder encoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    /**
     * Authenticates a user by username and password.
     * Sets the authenticated user in the SecurityContext.
     * @param loginRequest DTO containing username and password.
     * @return ResponseEntity with user information and roles on successful authentication.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // ... (existing code for /login) ...
        // In AuthController.java, within the /login method
        // Retrieve the authenticated user from the SecurityContext
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get the UserDetails from the authentication object
        // The custom UserDetailsServiceImpl should return our User class, not the default one.
        com.tritonptms.public_transport_management_system.model.User authenticatedUser =
                (com.tritonptms.public_transport_management_system.model.User) authentication.getPrincipal();

        // Extract the roles
        List<String> roles = authenticatedUser.getRoles().stream()
                .map(role -> role.getName().toString())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new UserInfoResponse(authenticatedUser.getId(), authenticatedUser.getUsername(), roles));
    }
    
    /**
     * Retrieves the current authenticated user's information.
     * @return ResponseEntity with the user's information or 401 Unauthorized.
     */
    @GetMapping("/me") // NEW ENDPOINT
    public ResponseEntity<?> getCurrentUser() {
        // In AuthController.java, within the /me method
        // Get the principal from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser")) {
        return ResponseEntity.status(401).body("Not authenticated");
        }

                // Assume that the principal is our custom User object
                com.tritonptms.public_transport_management_system.model.User currentUser =
                        (com.tritonptms.public_transport_management_system.model.User) authentication.getPrincipal();

                List<String> roles = currentUser.getRoles().stream()
                        .map(role -> role.getName().toString())
                        .collect(Collectors.toList());

                return ResponseEntity.ok(new UserInfoResponse(currentUser.getId(), currentUser.getUsername(), roles));
        }

    /**
     * Registers a new user with a default role.
     * @param signUpRequest DTO containing username, password, and (implicitly) role assignment logic.
     * @return ResponseEntity indicating success or failure of registration.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody LoginRequest signUpRequest) {
        // ... (existing code for /register) ...
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        User user = new User(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role defaultRole = roleRepository.findByName(ERole.ROLE_OPERATIONS_MANAGER.name())
                .orElseThrow(() -> new RuntimeException("Error: Default role ROLE_OPERATIONS_MANAGER not found. Ensure it's populated."));
        roles.add(defaultRole);

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully as OPERATIONS_MANAGER!");
    }
}