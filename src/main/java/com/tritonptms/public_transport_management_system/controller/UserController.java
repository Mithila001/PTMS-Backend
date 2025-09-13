package com.tritonptms.public_transport_management_system.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tritonptms.public_transport_management_system.dto.UserResponseDto;
import com.tritonptms.public_transport_management_system.dto.auth.RegisterRequestDto;
import com.tritonptms.public_transport_management_system.dto.auth.RegisterResponseDto;
import com.tritonptms.public_transport_management_system.model.User;
import com.tritonptms.public_transport_management_system.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDto> userDtos = users.stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    /**
     * Registers a new user with a default role.
     *
     * @param registerRequest DTO containing user registration details.
     * @return ResponseEntity with the generated username and password upon success.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDto registerRequest) {
        try {
            // The service now returns the DTO we need to send back
            RegisterResponseDto response = userService.registerNewUser(registerRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }
}