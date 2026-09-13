package com.tritonptms.ptms.feature.auth;

import com.tritonptms.ptms.feature.auth.dto.UserInfoResponse;
import com.tritonptms.ptms.infrastructure.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public UserInfoResponse currentUser(@AuthenticationPrincipal AuthenticatedUser principal) {
        return new UserInfoResponse(principal.id(), principal.getUsername(), principal.roles());
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }
}
