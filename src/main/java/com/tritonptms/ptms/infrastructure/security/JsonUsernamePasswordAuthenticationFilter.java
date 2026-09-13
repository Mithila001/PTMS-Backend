package com.tritonptms.ptms.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tritonptms.ptms.feature.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    public JsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/auth/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            throw new InvalidLoginRequestAuthenticationException("Login requires application/json.");
        }

        try {
            LoginRequest requestBody = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            if (requestBody.username() == null || requestBody.username().isBlank()
                    || requestBody.password() == null || requestBody.password().isBlank()) {
                throw new BadCredentialsException("Username and password are required");
            }

            UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                    requestBody.username().trim(), requestBody.password());
            setDetails(request, token);
            return getAuthenticationManager().authenticate(token);
        } catch (IOException ex) {
            throw new InvalidLoginRequestAuthenticationException("The login request body is not valid JSON.", ex);
        }
    }
}
