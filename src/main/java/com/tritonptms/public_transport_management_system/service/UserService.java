package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.User;

import java.util.List;

import com.tritonptms.public_transport_management_system.dto.user.RegisterRequestDto;
import com.tritonptms.public_transport_management_system.dto.user.RegisterResponseDto;
import com.tritonptms.public_transport_management_system.dto.user.UserUpdateDto;

public interface UserService {
    RegisterResponseDto registerNewUser(RegisterRequestDto registerRequest);

    List<User> getAllUsers();

    User getUserById(Long id);

    void deleteUserById(Long id);

    User updateUser(Long id, UserUpdateDto userUpdateDto);
}