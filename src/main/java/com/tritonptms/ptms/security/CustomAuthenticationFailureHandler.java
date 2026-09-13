package com.tritonptms.ptms.security;

import com.tritonptms.ptms.common.web.ApiProblemWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ApiProblemWriter problemWriter;

    public CustomAuthenticationFailureHandler(ApiProblemWriter problemWriter) {
        this.problemWriter = problemWriter;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        if (exception instanceof InvalidLoginRequestAuthenticationException) {
            problemWriter.write(response, HttpStatus.BAD_REQUEST, "invalid-login-request", "Invalid login request",
                    exception.getMessage());
            return;
        }
        if (exception instanceof InternalAuthenticationServiceException) {
            problemWriter.write(response, HttpStatus.INTERNAL_SERVER_ERROR, "authentication-service-error",
                    "Authentication service error", "Authentication could not be completed.");
            return;
        }

        problemWriter.write(response, HttpStatus.UNAUTHORIZED, "invalid-credentials", "Authentication failed",
                "Invalid username or password.");
    }
}
