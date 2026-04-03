package com.example.foundation.exception;

import com.example.foundation.api.BaseResponse;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST controllers
 * Handles validation exceptions from PreCheck using standard Java exceptions
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle illegal state exceptions (duplicate data, CREATE operations)
     * Returns 409 Conflict
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public BaseResponse<Void> handleIllegalStateException(IllegalStateException ex) {
        return BaseResponse.error(ex.getMessage());
    }
    
    /**
     * Handle validation errors from @Valid annotations
     * Returns 400 Bad Request with detailed field error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public BaseResponse<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        // Collect all field validation errors
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        // Create error message summary
        String message = "Validation failed: " + errors.entrySet().stream()
                .map(entry -> entry.getKey() + " - " + entry.getValue())
                .collect(Collectors.joining("; "));
        
        return new BaseResponse<>(
            "ERROR",
            message,
            errors
        );
    }
    
    /**
     * Handle illegal argument exceptions (data not found, UPDATE/DELETE operations)
     * Returns 404 Not Found
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public BaseResponse<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        return BaseResponse.error(ex.getMessage());
    }
    
    /**
     * Handle general exceptions
     * Returns 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public BaseResponse<Void> handleGeneralException(Exception ex) {
        return BaseResponse.error("An error occurred: " + ex.getMessage());
    }
}
