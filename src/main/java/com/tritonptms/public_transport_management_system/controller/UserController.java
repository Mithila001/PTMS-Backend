package com.tritonptms.public_transport_management_system.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tritonptms.public_transport_management_system.dto.UserResponseDto;
import com.tritonptms.public_transport_management_system.dto.auth.RegisterRequestDto;
import com.tritonptms.public_transport_management_system.dto.auth.RegisterResponseDto;
import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import com.tritonptms.public_transport_management_system.model.User;
import com.tritonptms.public_transport_management_system.service.UserService;
import com.tritonptms.public_transport_management_system.utils.BaseResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<UserResponseDto>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDto> userDtos = users.stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(BaseResponse.success(userDtos, "Users retrieved successfully"), HttpStatus.OK);
    }

    /**
     * Registers a new user with a default role.
     *
     * @param registerRequest DTO containing user registration details.
     * @return ResponseEntity with the generated username and password upon success.
     */
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<RegisterResponseDto>> registerUser(
            @RequestBody RegisterRequestDto registerRequest) {
        try {
            RegisterResponseDto responseDto = userService.registerNewUser(registerRequest);
            return new ResponseEntity<>(BaseResponse.success(responseDto, "User registered successfully!"),
                    HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(BaseResponse.error(null, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id The unique ID of the user.
     * @return A ResponseEntity containing the UserResponseDto.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserResponseDto userDto = new UserResponseDto(user);
        return new ResponseEntity<>(BaseResponse.success(userDto, "User retrieved successfully"), HttpStatus.OK);
    }

    /**
     * Deletes a user by their unique ID.
     *
     * @param id The unique ID of the user to be deleted.
     * @return A ResponseEntity indicating the success of the deletion.
     * @throws ResourceNotFoundException if the user does not exist.
     * @throws IllegalArgumentException  if an ADMIN tries to delete their own
     *                                   account.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(BaseResponse.success(null, "User deleted successfully"), HttpStatus.OK);
    }

}