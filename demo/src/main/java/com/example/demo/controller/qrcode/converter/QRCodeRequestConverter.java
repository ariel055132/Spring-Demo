package com.example.demo.controller.qrcode.converter;

import com.example.demo.controller.qrcode.dto.CreateQRCodeRequest;
import com.example.demo.controller.qrcode.dto.GenerateQRCodeRequest;
import com.example.demo.controller.qrcode.dto.UpdateQRCodeRequest;
import com.example.demo.service.qrcode.arg.CreateQRCodeArg;
import com.example.demo.service.qrcode.arg.DeleteQRCodeArg;
import com.example.demo.service.qrcode.arg.GenerateQRCodeArg;
import com.example.demo.service.qrcode.arg.UpdateQRCodeArg;
import org.springframework.stereotype.Component;

@Component
public class QRCodeRequestConverter {

    public GenerateQRCodeArg toGenerateArg(GenerateQRCodeRequest request) {
        return GenerateQRCodeArg.builder()
                .content(request.getContent())
                .width(request.getWidthOrDefault())
                .height(request.getHeightOrDefault())
                .build();
    }

    public CreateQRCodeArg toCreateArg(CreateQRCodeRequest request) {
        return CreateQRCodeArg.builder()
                .url(request.getUrl())
                .userId(request.getUserId())
                .width(request.getWidthOrDefault())
                .height(request.getHeightOrDefault())
                .expiresAt(request.getExpiresAt())
                .build();
    }

    public UpdateQRCodeArg toUpdateArg(String shortCode, UpdateQRCodeRequest request) {
        return UpdateQRCodeArg.builder()
                .shortCode(shortCode)
                .userId(request.getUserId())
                .url(request.getUrl())
                .expiresAt(request.getExpiresAt())
                .build();
    }

    public DeleteQRCodeArg toDeleteArg(String shortCode, String userId) {
        return DeleteQRCodeArg.builder()
                .shortCode(shortCode)
                .userId(userId)
                .build();
    }
}

