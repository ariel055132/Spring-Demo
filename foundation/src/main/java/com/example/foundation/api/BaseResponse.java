package com.example.foundation.api;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BaseResponse<T> {
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_ERROR = "ERROR";

    private String status;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    public BaseResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
    
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(STATUS_SUCCESS, "Operation completed successfully", data);
    }

    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>(STATUS_SUCCESS, message, data);
    }

    // Returns an error response with no payload (e.g. 404 Not Found, 409 Conflict)
    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>(STATUS_ERROR, message, null);
    }

    // Returns an error response with a payload (e.g. 400 Bad Request with field-level error details)
    public static <T> BaseResponse<T> error(String message, T data) {
        return new BaseResponse<>(STATUS_ERROR, message, data);
    }
}
