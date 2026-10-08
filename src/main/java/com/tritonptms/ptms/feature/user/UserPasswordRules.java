package com.tritonptms.ptms.feature.user;

import com.tritonptms.ptms.common.exception.BadRequestException;

import java.nio.charset.StandardCharsets;

public final class UserPasswordRules {

    private UserPasswordRules() {
    }

    public static void validate(String password) {
        if (password == null || password.codePointCount(0, password.length()) < 12
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadRequestException("Password must be at least 12 characters and at most 72 UTF-8 bytes.");
        }
    }
}
