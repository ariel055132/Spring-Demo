package com.example.demo.controller.qrcode.dto;

import com.example.foundation.api.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Request DTO for QR code generation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class QRCodeRequest extends BaseRequest {
    
    /**
     * Content to encode in QR code (required)
     * Maximum length: 4296 characters for QR code
     */
    @Schema(description = "Content to encode in QR code", example = "https://example.com")
    @NotBlank(message = "Content cannot be blank")
    @Size(max = 4296, message = "Content exceeds maximum length of 4296 characters")
    private String content;
    
    /**
     * Width of QR code image in pixels (optional, default: 300)
     * Range: 100-1000 pixels
     */
    @Schema(description = "Width of QR code image in pixels", example = "300", defaultValue = "300")
    @Min(value = 100, message = "Width must be at least 100 pixels")
    @Max(value = 1000, message = "Width must not exceed 1000 pixels")
    private Integer width;
    
    /**
     * Height of QR code image in pixels (optional, default: 300)
     * Range: 100-1000 pixels
     */
    @Schema(description = "Height of QR code image in pixels", example = "300", defaultValue = "300")
    @Min(value = 100, message = "Height must be at least 100 pixels")
    @Max(value = 1000, message = "Height must not exceed 1000 pixels")
    private Integer height;
    
    /**
     * Constructor for content only (uses default size)
     */
    public QRCodeRequest(String content) {
        this.content = content;
        this.width = 300;
        this.height = 300;
    }
    
    /**
     * Get width with default value if not set
     */
    public int getWidthOrDefault() {
        return width != null ? width : 300;
    }
    
    /**
     * Get height with default value if not set
     */
    public int getHeightOrDefault() {
        return height != null ? height : 300;
    }
}
