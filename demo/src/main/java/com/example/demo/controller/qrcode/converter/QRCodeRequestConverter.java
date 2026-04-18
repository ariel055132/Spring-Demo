package com.example.demo.controller.qrcode.converter;

import com.example.demo.controller.qrcode.dto.CreateQRCodeRequest;
import com.example.demo.controller.qrcode.dto.GenerateQRCodeRequest;
import com.example.demo.service.qrcode.arg.CreateQRCodeArg;
import com.example.demo.service.qrcode.arg.DeleteQRCodeArg;
import com.example.demo.service.qrcode.arg.GenerateQRCodeArg;
import org.springframework.stereotype.Component;

/**
 * Converter for QR code requests to service arguments
 */
@Component
public class QRCodeRequestConverter {
    
    /**
     * Convert GenerateQRCodeRequest to GenerateQRCodeArg
     * 
     * @param request QR code request from controller
     * @return Service argument for QR code generation
     */
    public GenerateQRCodeArg toGenerateArg(GenerateQRCodeRequest request) {
        return GenerateQRCodeArg.builder()
                .content(request.getContent())
                .width(request.getWidthOrDefault())
                .height(request.getHeightOrDefault())
                .build();
    }

    /**
     * Convert CreateQRCodeRequest to CreateQRCodeArg
     * 
     * @param shortCode
     * @param userId
     * @return
     */
    public CreateQRCodeArg toCreateArg(CreateQRCodeRequest request) {
        return CreateQRCodeArg.builder()
                .url(request.getUrl())
                .userId(request.getUserId())
                .width(request.getWidthOrDefault())
                .height(request.getHeightOrDefault())
                .build();
    }
    
    /**
     * Convert shortCode, and userId to DeleteQRCodeArg
     * 
     * @param shortCode
     * @param userId
     * @return
     */
    public DeleteQRCodeArg toDeleteArg(String shortCode, String userId) {
        return DeleteQRCodeArg.builder()
            .shortCode(shortCode)
            .userId(userId)
            .build();
    }
}
