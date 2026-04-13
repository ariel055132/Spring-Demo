package com.example.demo.controller.qrcode.dto;

import com.example.foundation.validator.Ascii;
import com.example.foundation.api.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating URL-based QR codes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateQRCodeRequest extends BaseRequest {
    
    /**
     * The URL to encode in the QR code (required)
     * Must be ASCII characters only, maximum 20 characters
     */
    @Schema(description = "URL to encode in QR code (ASCII only, max 20 chars)", 
            example = "https://abc.com", 
            maxLength = 20)
    @NotBlank(message = "URL cannot be blank")
    @Size(max = 20, message = "URL must not exceed 20 characters")
    @Ascii(message = "URL must contain only ASCII characters")
    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    private String url;
    
    /**
     * User ID who is creating this QR code
     */
    @Schema(description = "User ID", example = "user123")
    @NotBlank(message = "User ID cannot be blank")
    private String userId;
    
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
