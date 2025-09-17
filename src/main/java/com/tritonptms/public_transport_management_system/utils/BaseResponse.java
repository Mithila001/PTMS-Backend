package com.tritonptms.public_transport_management_system.utils;

import java.time.LocalDateTime;

public class BaseResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;
    private Object errors;

    public BaseResponse(int status, String message, T data, Object errors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<>(200, message, data, null);
    }

    public static <T> BaseResponse<T> error(T data, String message) {
        return new BaseResponse<>(400, message, null, null);
    }

    // Getters and setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }
}
