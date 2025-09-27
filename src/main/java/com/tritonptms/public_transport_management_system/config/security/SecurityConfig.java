// F:\OnGoinProject\Transport Management System\public-transport-management-system\src\main\java\com\tritonptms\public_transport_management_system\config\security\SecurityConfig.java

package com.tritonptms.public_transport_management_system.config.security;

import com.tritonptms.public_transport_management_system.service.security.UserDetailsServiceImpl;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Keep this
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${cors.allowed.origins:http://localhost:5173}")
    private String corsAllowedOrigins;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // You can keep this since it's good practice and your code already uses it.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // You can keep these custom handlers, they are valid
    @Bean
    public CustomAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public CustomAuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager)
            throws Exception {
        JsonUsernamePasswordAuthenticationFilter jsonFilter = new JsonUsernamePasswordAuthenticationFilter();
        jsonFilter.setAuthenticationManager(authManager);
        jsonFilter.setFilterProcessesUrl("/api/auth/login");
        jsonFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        jsonFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        http.addFilterAt(jsonFilter, UsernamePasswordAuthenticationFilter.class);
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/assignments/**").permitAll()
                        .requestMatchers("/api/scheduled-trips/**").permitAll()
                        .requestMatchers("/api/logs/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/enums/**").permitAll()
                        .requestMatchers("/api/buses/**").permitAll()
                        .requestMatchers("/api/drivers/**").permitAll()
                        .requestMatchers("/api/conductors/**").permitAll()
                        .requestMatchers("/api/employees/**").permitAll()
                        .requestMatchers("/api/routes/**").permitAll()
                        .requestMatchers("/api/audit/**").permitAll()
                        .requestMatchers("/api/action-logs/**").permitAll()
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/dashboard/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/operations/**").hasAnyRole("ADMIN", "OPERATIONS_MANAGER")
                        // .requestMatchers("/api/buses/**").hasAnyRole("ADMIN", "OPERATIONS_MANAGER")
                        .requestMatchers("/api/routes/**").hasAnyRole("ADMIN", "OPERATIONS_MANAGER")
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginProcessingUrl("/api/auth/login") // This is where Spring Security will handle the POST
                                                               // request
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler())
                        .permitAll())
                .exceptionHandling(exceptions -> exceptions
                        // This entry point is triggered when an unauthenticated user tries to access a
                        // protected resource.
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\": \"Unauthorized\", \"message\": \"Authentication is required to access this resource.\"}");
                        }));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Split the injected property by comma and trim whitespace
        List<String> origins = Arrays.stream(this.corsAllowedOrigins.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}