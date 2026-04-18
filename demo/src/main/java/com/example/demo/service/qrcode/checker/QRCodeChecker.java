package com.example.demo.service.qrcode.checker;

import com.example.demo.repository.QRCodeRepository;
import com.example.foundation.checker.BaseChecker;
import com.example.foundation.util.LogUtil;
import com.example.demo.entity.QRCode;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
@Component
public abstract class QRCodeChecker<T> extends BaseChecker<T> {
    @Autowired
    protected QRCodeRepository qrCodeRepository;
    
    
    // Cache to track recently generated QR codes (content -> timestamp)
    private final Map<String, Instant> qrCodeCache = new ConcurrentHashMap<>();
    
    // Time window for duplicate detection (in minutes)
    private static final long DUPLICATE_CHECK_WINDOW_MINUTES = 5;
    
    /**
     * Check if the QR code content has been generated recently
     * 
     * @param content The QR code content to check
     * @throws IllegalArgumentException if duplicate QR code is detected
     */
    private void checkForDuplicateQRCode(String content) {
        Instant now = Instant.now();
        Instant lastGenerated = qrCodeCache.get(content);
        
        if (lastGenerated != null) {
            long minutesSinceLastGeneration = ChronoUnit.MINUTES.between(lastGenerated, now);
            
            if (minutesSinceLastGeneration < DUPLICATE_CHECK_WINDOW_MINUTES) {
                LogUtil.wrongInfo("Duplicate QR code detected for content: {} (generated {} minutes ago)", 
                           content.substring(0, Math.min(50, content.length())), minutesSinceLastGeneration);
                throw new IllegalArgumentException(
                    "Duplicate QR code detected. This content was already generated " + 
                    minutesSinceLastGeneration + " minute(s) ago. Please wait before generating again."
                );
            }
        }
        
        // Store the current generation timestamp
        qrCodeCache.put(content, now);
        LogUtil.debugInfo("QR code content registered in cache: {}", content.substring(0, Math.min(50, content.length())));
    }
    
    /**
     * Clean up expired entries from the cache to prevent memory leaks
     */
    private void cleanupExpiredEntries() {
        Instant expirationThreshold = Instant.now().minus(DUPLICATE_CHECK_WINDOW_MINUTES, ChronoUnit.MINUTES);
        
        qrCodeCache.entrySet().removeIf(entry -> {
            if (entry.getValue().isBefore(expirationThreshold)) {
                LogUtil.debugInfo("Removing expired QR code cache entry: {}", 
                            entry.getKey().substring(0, Math.min(50, entry.getKey().length())));
                return true;
            }
            return false;
        });
    }
    
    /**
     * Get the current size of the QR code cache (for monitoring)
     * 
     * @return Number of cached QR codes
     */
    public int getCacheSize() {
        return qrCodeCache.size();
    }
    
    /**
     * Clear the QR code cache (for testing or administrative purposes)
     */
    public void clearCache() {
        LogUtil.addInfo("Clearing QR code cache ({} entries)", qrCodeCache.size());
        qrCodeCache.clear();
    }

    /**
     * Determine whether QRCode is exist with userId and originalUrl only
     * 
     * @param userId
     * @param originalUrl
     * @return
     */
    protected boolean isQRCodeExistByUserIdAndOriginalUrl(String userId, String originalUrl) {
        Optional<QRCode> qrCode = qrCodeRepository.findByUserIdAndOriginalUrl(userId, originalUrl);
        return qrCode.isEmpty() ? false : true;
    }

    /**
     * Determine whether QRCode is exist with shortCode only
     * 
     * @param shortCode
     * @return
     */
    protected boolean isQRCodeExistByShortCode(String shortCode) {
        Optional<QRCode> qrCode = qrCodeRepository.findByShortCode(shortCode);
        return qrCode.isEmpty() ? false : true;
    }

    /**
     * Determine whether QRCode is exist with userId only
     * 
     * @param userId
     * @return
     */
    protected boolean isQRCodeExistByUserId(String userId) {
        List<QRCode> qrCodes = qrCodeRepository.findByUserId(userId);
        return qrCodes.isEmpty() ? false : true;
    }

    /**
     * Determine whether QRCode is exist with shortCode and userId
     * 
     * @param shortCode
     * @param userId
     * @return
     */
    protected boolean isQRCodeExist(String shortCode, String userId) {
        return qrCodeRepository.findByUserIdAndShortCode(userId, shortCode).isPresent();
    }
}
