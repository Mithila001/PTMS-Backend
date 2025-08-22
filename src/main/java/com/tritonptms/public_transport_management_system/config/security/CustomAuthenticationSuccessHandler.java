// F:\OnGoinProject\Transport Management System\public-transport-management-system\src\main\java\com\tritonptms\public_transport_management_system\config\security\CustomAuthenticationSuccessHandler.java

package com.tritonptms.public_transport_management_system.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tritonptms.public_transport_management_system.model.User; // <-- Keep this
import com.tritonptms.public_transport_management_system.dto.auth.UserInfoResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Get the authenticated user principal from the SecurityContext
        // We know this is our custom User object because of our UserDetailsServiceImpl changes
        User user = (User) authentication.getPrincipal();

        // Convert the user's roles from a Set<Role> to a List<String>
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName()) // Map each Role object to its name String
                .collect(Collectors.toList());

        // Create a DTO to send back to the frontend
        UserInfoResponse userInfo = new UserInfoResponse(user.getId(), user.getUsername(), roles);

        // Set response headers and write JSON
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(userInfo));
    }
}