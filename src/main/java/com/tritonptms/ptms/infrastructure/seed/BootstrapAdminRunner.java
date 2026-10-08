package com.tritonptms.ptms.infrastructure.seed;

import com.tritonptms.ptms.common.exception.ConflictException;
import com.tritonptms.ptms.feature.user.ERole;
import com.tritonptms.ptms.feature.user.RoleRepository;
import com.tritonptms.ptms.feature.user.User;
import com.tritonptms.ptms.feature.user.UserPasswordRules;
import com.tritonptms.ptms.feature.user.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@Profile("prod")
@ConditionalOnProperty(prefix = "app.bootstrap", name = "enabled", havingValue = "true")
public class BootstrapAdminRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final BootstrapAdmin details;

    public BootstrapAdminRunner(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, Validator validator,
            @Value("${APP_BOOTSTRAP_USERNAME:}") String username,
            @Value("${APP_BOOTSTRAP_PASSWORD:}") String password,
            @Value("${APP_BOOTSTRAP_EMAIL:}") String email,
            @Value("${APP_BOOTSTRAP_FIRST_NAME:}") String firstName,
            @Value("${APP_BOOTSTRAP_LAST_NAME:}") String lastName,
            @Value("${APP_BOOTSTRAP_NIC:}") String nic) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.details = new BootstrapAdmin(username, password, email, firstName, lastName, nic);
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsWithRole(ERole.ROLE_ADMIN.name())) {
            return;
        }
        Set<ConstraintViolation<BootstrapAdmin>> violations = validator.validate(details);
        if (!violations.isEmpty()) {
            throw new IllegalStateException("Bootstrap administrator configuration is incomplete or invalid.");
        }
        UserPasswordRules.validate(details.password());
        String username = details.username().trim();
        String email = details.email().trim().toLowerCase(java.util.Locale.ROOT);
        String nic = details.nic().trim();
        if (userRepository.existsByUsername(username)
                || userRepository.existsByEmail(email)
                || userRepository.existsByNic(nic)) {
            throw new ConflictException("Bootstrap administrator details match an existing non-admin user.");
        }
        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(details.password()));
        admin.setEmail(email);
        admin.setFirstName(details.firstName().trim());
        admin.setLastName(details.lastName().trim());
        admin.setNic(nic);
        admin.setRoles(new HashSet<>(Set.of(roleRepository.findByName(ERole.ROLE_ADMIN.name())
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN reference data is missing.")))));
        userRepository.save(admin);
    }

    private record BootstrapAdmin(
            @NotBlank @Size(max = 50) String username,
            @NotBlank String password,
            @NotBlank @Email @Size(max = 100) String email,
            @NotBlank @Size(max = 50) String firstName,
            @NotBlank @Size(max = 50) String lastName,
            @NotBlank @Size(max = 15) String nic) {
    }
}
