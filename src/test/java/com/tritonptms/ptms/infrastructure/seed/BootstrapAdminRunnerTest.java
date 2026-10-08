package com.tritonptms.ptms.infrastructure.seed;

import com.tritonptms.ptms.feature.user.ERole;
import com.tritonptms.ptms.feature.user.Role;
import com.tritonptms.ptms.feature.user.RoleRepository;
import com.tritonptms.ptms.feature.user.User;
import com.tritonptms.ptms.feature.user.UserRepository;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BootstrapAdminRunnerTest {

    @Test
    void createsHashedAdminOnlyWhenNoAdminExists() {
        UserRepository users = mock(UserRepository.class);
        RoleRepository roles = mock(RoleRepository.class);
        var encoder = new BCryptPasswordEncoder();
        var role = new Role(ERole.ROLE_ADMIN.name());
        when(users.existsWithRole(ERole.ROLE_ADMIN.name())).thenReturn(false);
        when(roles.findByName(ERole.ROLE_ADMIN.name())).thenReturn(Optional.of(role));
        AtomicReference<User> saved = new AtomicReference<>();
        when(users.save(any(User.class))).thenAnswer(invocation -> {
            saved.set(invocation.getArgument(0));
            return invocation.getArgument(0);
        });
        var validatorFactory = Validation.buildDefaultValidatorFactory();
        try {
            var runner = new BootstrapAdminRunner(users, roles, encoder, validatorFactory.getValidator(),
                    "first.admin", "FirstAdminPassword1!", "admin@example.test", "First", "Admin", "200000000001");

            runner.run(new DefaultApplicationArguments(new String[0]));

            assertThat(saved.get().getUsername()).isEqualTo("first.admin");
            assertThat(encoder.matches("FirstAdminPassword1!", saved.get().getPassword())).isTrue();
            assertThat(saved.get().getRoles()).contains(role);
        } finally {
            validatorFactory.close();
        }
    }

    @Test
    void leavesExistingAdminAloneWithoutRequiringBootstrapCredentials() {
        UserRepository users = mock(UserRepository.class);
        when(users.existsWithRole(ERole.ROLE_ADMIN.name())).thenReturn(true);
        var validatorFactory = Validation.buildDefaultValidatorFactory();
        try {
            var runner = new BootstrapAdminRunner(users, mock(RoleRepository.class), new BCryptPasswordEncoder(),
                    validatorFactory.getValidator(), "", "", "", "", "", "");
            runner.run(new DefaultApplicationArguments(new String[0]));
            verify(users, never()).save(any(User.class));
        } finally {
            validatorFactory.close();
        }
    }
}
