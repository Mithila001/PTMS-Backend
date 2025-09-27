package com.tritonptms.public_transport_management_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tritonptms.public_transport_management_system.config.StartupPropertyValidator;

@SpringBootApplication
@EnableJpaAuditing
public class PublicTransportManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(PublicTransportManagementSystemApplication.class);
        application.addInitializers(new StartupPropertyValidator());
        application.run(args);
    }

    // @Bean
    // public WebMvcConfigurer corsConfigurer() {
    // return new WebMvcConfigurer() {
    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    // registry.addMapping("/api/**") // Apply to all API endpoints
    // .allowedOrigins("http://localhost:5173/") // Allow requests from React app's
    // port
    // .allowedMethods("GET", "POST", "PUT", "DELETE"); // Allow these HTTP methods
    // }
    // };
    // }

}
