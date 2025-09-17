package com.tritonptms.public_transport_management_system.dto.user;

public class RegisterResponseDto {

    private String username;
    private String password;
    private String message;

    public RegisterResponseDto(String username, String password, String message) {
        this.username = username;
        this.password = password;
        this.message = message;
    }

    // Getters and Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}