package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseRequest {
    private String requestId;
    private LocalDateTime timestamp;
    
    public BaseRequest(String requestId) {
        this.requestId = requestId;
        this.timestamp = LocalDateTime.now();
    }
}
