package com.example.demo.controller.qrcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for QR code details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QRCodeDetailResponse {
    
    @Schema(description = "QR code ID", example = "1")
    private Long id;
    
    @Schema(description = "Short code for QR redirect", example = "abc123")
    private String shortCode;
    
    @Schema(description = "Original URL", example = "https://abc.com")
    private String originalUrl;
    
    @Schema(description = "QR code image as Base64 string")
    private String qrCodeImage;
    
    @Schema(description = "User ID who created this QR code", example = "user123")
    private String userId;
    
    @Schema(description = "Width of QR code image", example = "300")
    private Integer width;
    
    @Schema(description = "Height of QR code image", example = "300")
    private Integer height;
    
    @Schema(description = "Number of times scanned", example = "5")
    private Long scanCount;
    
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Last scanned timestamp")
    private LocalDateTime lastScannedAt;
    
    @Schema(description = "Full redirect URL", example = "http://localhost:8080/api/qrcode/r/abc123")
    private String redirectUrl;
}
