package com.example.demo.service.qrcode.arg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Service argument for QR code generation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateQRCodeArg {
    
    /**
     * Content to encode in QR code
     */
    private String content;
    
    /**
     * Width of QR code image in pixels
     */
    private int width;
    
    /**
     * Height of QR code image in pixels
     */
    private int height;
}
