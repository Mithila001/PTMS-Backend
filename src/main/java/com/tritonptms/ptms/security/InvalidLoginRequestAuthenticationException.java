package com.tritonptms.ptms.security;

import org.springframework.security.authentication.AuthenticationServiceException;

public class InvalidLoginRequestAuthenticationException extends AuthenticationServiceException {
    public InvalidLoginRequestAuthenticationException(String message) {
        super(message);
    }

    public InvalidLoginRequestAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
