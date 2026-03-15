package com.example.demo.foundation.exception;

import com.example.demo.foundation.api.BaseResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Global exception handler for REST controllers
 * Handles custom validation exceptions from PreCheck
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle duplicate data exceptions (CREATE operations)
     * Returns 409 Conflict
     */
    @ExceptionHandler(DuplicateDataException.class)
    @ResponseBody
    public ResponseEntity<BaseResponse<Void>> handleDuplicateDataException(DuplicateDataException ex) {
        BaseResponse<Void> response = BaseResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    /**
     * Handle data not found exceptions (UPDATE/DELETE operations)
     * Returns 404 Not Found
     */
    @ExceptionHandler(DataNotFoundException.class)
    @ResponseBody
    public ResponseEntity<BaseResponse<Void>> handleDataNotFoundException(DataNotFoundException ex) {
        BaseResponse<Void> response = BaseResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * Handle general exceptions
     * Returns 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<BaseResponse<Void>> handleGeneralException(Exception ex) {
        BaseResponse<Void> response = BaseResponse.error("An error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
