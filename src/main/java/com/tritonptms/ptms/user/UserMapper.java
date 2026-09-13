package com.tritonptms.ptms.user;

import com.tritonptms.ptms.user.dto.CreateUserRequest;
import com.tritonptms.ptms.user.dto.UpdateUserRequest;
import com.tritonptms.ptms.user.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User fromCreateRequest(CreateUserRequest request) {
        User user = new User();
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setNic(request.nic().trim());
        return user;
    }

    public void applyUpdate(User user, UpdateUserRequest request) {
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setNic(request.nic().trim());
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getNic(),
                user.getRoles().stream().map(Role::getName).sorted().toList());
    }
}
