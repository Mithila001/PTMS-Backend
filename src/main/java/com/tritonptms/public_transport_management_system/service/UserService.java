package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.User;

import java.util.List;

import com.tritonptms.public_transport_management_system.dto.auth.RegisterRequestDto;
import com.tritonptms.public_transport_management_system.dto.auth.RegisterResponseDto;

public interface UserService {
    RegisterResponseDto registerNewUser(RegisterRequestDto registerRequest);

    List<User> getAllUsers();
}