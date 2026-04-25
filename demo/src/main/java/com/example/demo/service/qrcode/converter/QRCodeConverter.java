package com.example.demo.service.qrcode.converter;

import org.springframework.stereotype.Component;

import com.example.demo.service.qrcode.arg.CreateQRCodeArg;
import com.example.demo.service.qrcode.arg.GenerateQRCodeArg;
import com.example.demo.service.qrcode.response.QRCodeDetailResponse;
import com.example.demo.service.qrcode.response.QRCodeResponse;
import com.example.demo.entity.QRCode;

@Component
public class QRCodeConverter {
    /**
     * Convert GenerateQRCodeArg, base64 qrCode
     * 
     * @param arg
     * @param qrCodeImage
     * @param base64Image
     * @return
     */
    public QRCodeResponse toQRCodeResponse(GenerateQRCodeArg arg, byte[] qrCodeImage, String base64Image) {
        return QRCodeResponse.builder()
                .qrcode(base64Image)
                .format("PNG")
                .encoding("Base64")
                .width(arg.getWidth())
                .height(arg.getHeight())
                .contentLength(arg.getContent().length())
                .imageSizeBytes(qrCodeImage.length)
                .build();
    }

    /**
     * Map QRCode entity to QRCodeDetailResponse
     *
     * @param qrCode      QRCode entity
     * @param base64Image String
     * @param redirectUrl String
     * @param qrCodeUrl   URL for fetching the QR image
     * @return
     */
    public QRCodeDetailResponse mapToDetailResponse(QRCode qrCode, String base64Image, String redirectUrl, String qrCodeUrl) {
        return QRCodeDetailResponse.builder()
                .shortCode(qrCode.getShortCode())
                .originalUrl(qrCode.getOriginalUrl())
                .qrCodeImage(base64Image)
                .userId(qrCode.getUserId())
                .width(qrCode.getWidth())
                .height(qrCode.getHeight())
                .scanCount(qrCode.getScanCount())
                .redirectUrl(redirectUrl)
                .qrCodeUrl(qrCodeUrl)
                .expiresAt(qrCode.getExpiresAt())
                .isDeleted(qrCode.isDeleted())
                .build();
    }

    /**
     * CreateQRCodeArg to QRCode entity converter
     *
     * @param arg           CreateQRCodeArg
     * @param shortCode     generated token
     * @param normalizedUrl validated and normalized URL
     * @return
     */
    public QRCode createArgtoQRCode(CreateQRCodeArg arg, String shortCode, String normalizedUrl) {
        return QRCode.builder()
            .shortCode(shortCode)
            .originalUrl(normalizedUrl)
            .width(arg.getWidth())
            .height(arg.getHeight())
            .userId(arg.getUserId())
            .expiresAt(arg.getExpiresAt())
            .isDeleted(false)
            .scanCount(0L)
            .build();
    }
}
