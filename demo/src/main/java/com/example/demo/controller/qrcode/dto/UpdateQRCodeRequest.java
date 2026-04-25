package com.example.demo.controller.qrcode.dto;

import com.example.foundation.api.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for updating a QR code's target URL and/or expiration.
 * Matches the UpdateRequest schema in the qr_code_generator reference implementation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateQRCodeRequest extends BaseRequest {

    @Schema(description = "New target URL (optional)", example = "https://new-url.com")
    @Size(max = 2048, message = "URL must not exceed 2048 characters")
    @Pattern(regexp = "^(https?://.*)?$", message = "URL must start with http:// or https://")
    private String url;

    @Schema(description = "New expiration timestamp (optional)")
    private LocalDateTime expiresAt;

    @Schema(description = "User ID for ownership verification", example = "user123")
    private String userId;
}
