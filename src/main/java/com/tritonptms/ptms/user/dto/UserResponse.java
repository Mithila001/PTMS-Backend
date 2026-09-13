package com.tritonptms.ptms.user.dto;

import java.util.List;

public record UserResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        String nic,
        List<String> roles) {
}
