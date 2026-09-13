package com.tritonptms.ptms.security;

import com.tritonptms.ptms.common.web.ApiProblemWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ApiProblemWriter problemWriter;

    public RestAuthenticationEntryPoint(ApiProblemWriter problemWriter) {
        this.problemWriter = problemWriter;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        problemWriter.write(response, HttpStatus.UNAUTHORIZED, "unauthorized", "Unauthorized",
                "Authentication is required to access this resource.");
    }
}
