package com.example.foundation.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private String status;          // SUCCESS or ERROR
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
        return new BaseResponse<>("SUCCESS", "Operation completed successfully", data);
    }
    
    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>("SUCCESS", message, data);
    }
    
    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>("ERROR", message, null);
    }
}
