package com.example.demo.controller.qrcode.converter;

import com.example.demo.controller.qrcode.dto.QRCodeRequest;
import com.example.demo.service.qrcode.arg.GenerateQRCodeArg;
import org.springframework.stereotype.Component;

/**
 * Converter for QR code requests to service arguments
 */
@Component
public class QRCodeRequestConverter {
    
    /**
     * Convert QRCodeRequest to GenerateQRCodeArg
     * 
     * @param request QR code request from controller
     * @return Service argument for QR code generation
     */
    public GenerateQRCodeArg toGenerateArg(QRCodeRequest request) {
        return GenerateQRCodeArg.builder()
                .content(request.getContent())
                .width(request.getWidthOrDefault())
                .height(request.getHeightOrDefault())
                .build();
    }
}
