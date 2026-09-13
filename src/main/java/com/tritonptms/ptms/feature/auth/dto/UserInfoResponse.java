package com.tritonptms.ptms.feature.auth.dto;

import java.util.List;

public record UserInfoResponse(Long id, String username, List<String> roles) {
}
