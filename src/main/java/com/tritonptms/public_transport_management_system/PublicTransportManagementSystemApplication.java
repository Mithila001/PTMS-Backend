package com.tritonptms.public_transport_management_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class PublicTransportManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(PublicTransportManagementSystemApplication.class, args);
	}

	// @Bean
    // public WebMvcConfigurer corsConfigurer() {
    //     return new WebMvcConfigurer() {
    //         @Override
    //         public void addCorsMappings(CorsRegistry registry) {
    //             registry.addMapping("/api/**") // Apply to all API endpoints
    //                     .allowedOrigins("http://localhost:5173/") // Allow requests from React app's port
    //                     .allowedMethods("GET", "POST", "PUT", "DELETE"); // Allow these HTTP methods
    //         }
    //     };
    // }

	

}
