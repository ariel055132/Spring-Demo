package com.example.demo.service.qrcode.checker;

import com.example.demo.repository.QRCodeRepository;
import com.example.foundation.checker.BaseChecker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 */
@Component
public abstract class QRCodeChecker<T> extends BaseChecker<T> {
    @Autowired
    protected QRCodeRepository qrCodeRepository;



    /**
     * Determine whether QRCode is exist with userId and originalUrl only
     * 
     * @param userId
     * @param originalUrl
     * @return
     */
    protected boolean isQRCodeExistByUserIdAndOriginalUrl(String userId, String originalUrl) {
        return qrCodeRepository.findByUserIdAndOriginalUrlAndIsDeletedFalse(userId, originalUrl).isPresent();
    }

    /**
     * Determine whether QRCode is exist with shortCode only
     * 
     * @param shortCode
     * @return
     */
    protected boolean isQRCodeExistByShortCode(String shortCode) {
        return qrCodeRepository.findByShortCodeAndIsDeletedFalse(shortCode).isPresent();
    }

    /**
     * Determine whether QRCode is exist with userId only
     * 
     * @param userId
     * @return
     */
    protected boolean isQRCodeExistByUserId(String userId) {
        return !qrCodeRepository.findByUserIdAndIsDeletedFalse(userId).isEmpty();
    }

    /**
     * Determine whether QRCode is exist with shortCode and userId
     * 
     * @param shortCode
     * @param userId
     * @return
     */
    protected boolean isQRCodeExist(String shortCode, String userId) {
        return qrCodeRepository.findByUserIdAndShortCodeAndIsDeletedFalse(userId, shortCode).isPresent();
    }
}
