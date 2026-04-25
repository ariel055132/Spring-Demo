package com.example.demo.service.qrcode.arg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQRCodeArg {
    private String shortCode;
    private String userId;
    private String url;
    private LocalDateTime expiresAt;
}
