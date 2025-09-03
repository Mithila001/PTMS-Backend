package com.tritonptms.public_transport_management_system.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.tritonptms.public_transport_management_system.utils.BaseResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Automatically thrown by Spring when an incoming request body fails
    // validation.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        BaseResponse<Object> errorResponse = new BaseResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                null,
                errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Custom exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        BaseResponse<String> errorResponse = new BaseResponse<>(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null,
                null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<String>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        String userFriendlyMessage = "This resource cannot be deleted as it is linked to other records.";

        // Try to get the root cause of the exception
        Throwable mostSpecificCause = ex.getMostSpecificCause();
        if (mostSpecificCause != null) {
            String errorMessage = mostSpecificCause.getMessage();

            // Regex for PostgreSQL foreign key violation
            Pattern pgPattern = Pattern.compile("Key \\(([^)]+)\\) is still referenced from table \"(\\w+)\"\\.");
            Matcher pgMatcher = pgPattern.matcher(errorMessage);

            if (pgMatcher.find()) {
                String childTable = pgMatcher.group(2);
                String childTableName = childTable.substring(0, 1).toUpperCase() + childTable.substring(1);
                userFriendlyMessage = String.format(
                        "Cannot delete this record because it is referenced by data in the '%s' table.",
                        childTableName);
            } else {
                // Generic regex for a "violates foreign key constraint" message
                Pattern genericPattern = Pattern
                        .compile("violates foreign key constraint \"(\\w+)\" on table \"(\\w+)\"");
                Matcher genericMatcher = genericPattern.matcher(errorMessage);

                if (genericMatcher.find()) {
                    String childTable = genericMatcher.group(2);
                    String childTableName = childTable.substring(0, 1).toUpperCase() + childTable.substring(1);
                    userFriendlyMessage = String.format(
                            "Cannot delete this record because it is referenced by data in the '%s' table.",
                            childTableName);
                }
            }
        }

        BaseResponse<String> errorResponse = new BaseResponse<>(
                HttpStatus.CONFLICT.value(),
                userFriendlyMessage,
                null,
                null);

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Fallback for any other exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<String>> handleRuntimeException(RuntimeException ex) {
        BaseResponse<String> errorResponse = new BaseResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid request: " + ex.getMessage(),
                null,
                null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}