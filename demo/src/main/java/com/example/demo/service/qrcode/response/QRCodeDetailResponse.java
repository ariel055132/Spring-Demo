package com.example.demo.service.qrcode.response;

import com.example.foundation.api.BaseResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Response DTO for QR code details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class QRCodeDetailResponse extends BaseResponse<Object> {
    
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

    @Schema(description = "Full redirect URL", example = "http://localhost:8081/api/qrcode/r/abc123")
    private String redirectUrl;

    @Schema(description = "URL to retrieve the QR code PNG image", example = "http://localhost:8081/api/qrcode/abc123/image")
    private String qrCodeUrl;

    @Schema(description = "Optional expiration timestamp")
    private java.time.LocalDateTime expiresAt;

    @Schema(description = "Whether this QR code has been soft-deleted", example = "false")
    private boolean isDeleted;
}
