// F:\OnGoinProject\Transport Management System\public-transport-management-system\src\main\java\com\tritonptms\public_transport_management_system\config\security\CustomAuthenticationSuccessHandler.java

package com.tritonptms.public_transport_management_system.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tritonptms.public_transport_management_system.model.User; // <-- Keep this
import com.tritonptms.public_transport_management_system.dto.auth.UserInfoResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // ensure SecurityContext is available
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // create session and store SecurityContext so JSESSIONID is set and persisted
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        // build response DTO (keep your existing logic)
        User user = (User) authentication.getPrincipal();
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());
        UserInfoResponse userInfo = new UserInfoResponse(user.getId(), user.getUsername(), roles);

        // debug: print session id (remove later)
        System.out.println("Login success. Session id: " + session.getId());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(userInfo));

        System.out.println(">> Set-Cookie header check: " + response.getHeader("Set-Cookie"));

    }

}