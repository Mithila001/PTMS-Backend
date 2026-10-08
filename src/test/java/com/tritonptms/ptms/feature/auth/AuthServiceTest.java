package com.tritonptms.ptms.feature.auth;

import com.tritonptms.ptms.common.exception.BadRequestException;
import com.tritonptms.ptms.feature.user.User;
import com.tritonptms.ptms.feature.user.UserRepository;
import com.tritonptms.ptms.infrastructure.security.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;

    @Test
    void changesPasswordAndAdvancesAuthenticationVersion() {
        var encoder = new BCryptPasswordEncoder();
        var user = new User("showcase.user", encoder.encode("CurrentPassword1!"));
        user.setId(11L);
        user.setAuthVersion(3L);
        when(userRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(user));
        var principal = new AuthenticatedUser(11L, user.getUsername(), user.getPassword(), 3L, java.util.List.of());

        new AuthService(userRepository, encoder).changePassword(principal, "CurrentPassword1!", "NextPassword2!");

        assertThat(encoder.matches("NextPassword2!", user.getPassword())).isTrue();
        assertThat(user.getAuthVersion()).isEqualTo(4L);
        verify(userRepository).findByIdForUpdate(11L);
    }

    @Test
    void rejectsWrongCurrentPasswordWithoutChangingVersion() {
        var encoder = new BCryptPasswordEncoder();
        var user = new User("showcase.user", encoder.encode("CurrentPassword1!"));
        user.setId(11L);
        user.setAuthVersion(3L);
        when(userRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(user));
        var principal = new AuthenticatedUser(11L, user.getUsername(), user.getPassword(), 3L, java.util.List.of());

        assertThatThrownBy(() -> new AuthService(userRepository, encoder)
                .changePassword(principal, "incorrect", "NextPassword2!"))
                .isInstanceOf(BadRequestException.class);
        assertThat(user.getAuthVersion()).isEqualTo(3L);
    }
}
