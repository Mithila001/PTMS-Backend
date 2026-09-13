package com.tritonptms.ptms.auth.dto;

import java.util.List;

public record UserInfoResponse(Long id, String username, List<String> roles) {
}
