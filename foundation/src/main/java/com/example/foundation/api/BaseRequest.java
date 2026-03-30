package com.example.foundation.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseRequest {
    private RequestHeader header;
    
    public BaseRequest(String requestId) {
        this.header = new RequestHeader(requestId);
    }
}
