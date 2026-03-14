package com.example.demo.util.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestHeader {
    private String requestId;
    private LocalDateTime timestamp;
    
    public RequestHeader(String requestId) {
        this.requestId = requestId;
        this.timestamp = LocalDateTime.now();
    }
}
