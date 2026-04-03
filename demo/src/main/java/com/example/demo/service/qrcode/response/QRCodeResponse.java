package com.example.demo.service.qrcode.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for QR code generation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QRCodeResponse {
    
    /**
     * Base64-encoded QR code image (PNG format)
     */
    private String qrcode;
    
    /**
     * Image format (always PNG)
     */
    private String format;
    
    /**
     * Encoding type (always Base64)
     */
    private String encoding;
    
    /**
     * Width of generated QR code in pixels
     */
    private int width;
    
    /**
     * Height of generated QR code in pixels
     */
    private int height;
    
    /**
     * Length of original content
     */
    private int contentLength;
    
    /**
     * Size of image in bytes
     */
    private int imageSizeBytes;
}
