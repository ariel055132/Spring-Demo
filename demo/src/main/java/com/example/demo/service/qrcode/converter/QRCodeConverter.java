package com.example.demo.service.qrcode.converter;

import org.springframework.stereotype.Component;

import com.example.demo.service.qrcode.arg.GenerateQRCodeArg;
import com.example.demo.service.qrcode.response.QRCodeDetailResponse;
import com.example.demo.service.qrcode.response.QRCodeResponse;
import com.example.demo.entity.QRCode;

@Component
public class QRCodeConverter {
    /**
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
     * @param qrCode QRCode entity
     * @param base64Image
     * @param redirectUrl
     * @return
     */
    public QRCodeDetailResponse mapToDetailResponse(QRCode qrCode, String base64Image, String redirectUrl) {
        return QRCodeDetailResponse.builder()
                .shortCode(qrCode.getShortCode())
                .originalUrl(qrCode.getOriginalUrl())
                .qrCodeImage(base64Image)
                .userId(qrCode.getUserId())
                .width(qrCode.getWidth())
                .height(qrCode.getHeight())
                .scanCount(qrCode.getScanCount())
                .redirectUrl(redirectUrl)
                .build();
    }
}
