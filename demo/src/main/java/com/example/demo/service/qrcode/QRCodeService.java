package com.example.demo.service.qrcode;

import com.example.demo.controller.qrcode.dto.CreateQRCodeRequest;
import com.example.demo.controller.qrcode.dto.QRCodeDetailResponse;
import com.example.demo.entity.QRCode;
import com.example.demo.repository.QRCodeRepository;
import com.example.demo.service.qrcode.arg.GenerateQRCodeArg;
import com.example.demo.service.qrcode.checker.QRCodeChecker;
import com.example.demo.service.qrcode.response.QRCodeResponse;
import com.example.foundation.api.BaseResponse;
import com.example.foundation.checker.PreCheck;
import com.example.foundation.util.LogUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Service for generating and managing QR codes
 */
@Service
public class QRCodeService {

    @Autowired
    private QRCodeRepository qrCodeRepository;
    
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Value("${app.qrcode.shortcode-length:6}")
    private int shortCodeLength;
    
    @Value("${app.qrcode.valkey-ttl:86400}")
    private long valkeyTtlSeconds; // Default 24 hours
    
    private static final String SHORT_CODE_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();
    private static final String VALKEY_KEY_PREFIX = "qrcode:";

    /**
     * Generate QR code and return as Base64-encoded response
     * 
     * @param arg Service argument containing QR code parameters
     * @return BaseResponse with QRCodeResponse data
     */
    @PreCheck(QRCodeChecker.class)
    public BaseResponse<QRCodeResponse> generateQRCode(GenerateQRCodeArg arg) {
        try {
            LogUtil.addInfo("Generating QR code for content length: {}, size: {}x{}", 
                       arg.getContent().length(), arg.getWidth(), arg.getHeight());
            
            // Generate QR code image bytes
            byte[] qrCodeImage = generateQRCodeImage(arg.getContent(), arg.getWidth(), arg.getHeight());
            
            // Encode to Base64
            String base64Image = Base64.getEncoder().encodeToString(qrCodeImage);
            
            // Build response
            QRCodeResponse response = QRCodeResponse.builder()
                    .qrcode(base64Image)
                    .format("PNG")
                    .encoding("Base64")
                    .width(arg.getWidth())
                    .height(arg.getHeight())
                    .contentLength(arg.getContent().length())
                    .imageSizeBytes(qrCodeImage.length)
                    .build();
            
            LogUtil.addInfo("QR code generated successfully, size: {} bytes", qrCodeImage.length);
            return BaseResponse.success("QR code generated successfully", response);
            
        } catch (WriterException e) {
            LogUtil.wrongInfo("Failed to encode QR code: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to encode QR code: " + e.getMessage());
        } catch (IOException e) {
            LogUtil.wrongInfo("Failed to write QR code image: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to write QR code image: " + e.getMessage());
        } catch (Exception e) {
            LogUtil.wrongInfo("Unexpected error generating QR code: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to generate QR code: " + e.getMessage());
        }
    }

    /**
     * Generate QR code as raw image bytes (for direct image response)
     * 
     * @param arg Service argument containing QR code parameters
     * @return QR code image as byte array
     * @throws WriterException if QR code generation fails
     * @throws IOException if image writing fails
     */
    public byte[] generateQRCodeImage(GenerateQRCodeArg arg) throws WriterException, IOException {
        return generateQRCodeImage(arg.getContent(), arg.getWidth(), arg.getHeight());
    }

    /**
     * Generate QR code image as byte array (PNG format)
     * 
     * @param content The content to encode in QR code
     * @param width Width of QR code image
     * @param height Height of QR code image
     * @return QR code image as byte array
     * @throws WriterException if QR code generation fails
     * @throws IOException if image writing fails
     */
    private byte[] generateQRCodeImage(String content, int width, int height) throws WriterException, IOException {
        // Configure QR code parameters
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // High error correction
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1); // Margin around QR code

        // Generate QR code matrix
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        // Convert to image
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Write image to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", outputStream);
        
        return outputStream.toByteArray();
    }
    
    // ==================== QR Code Management Methods ====================
    
    /**
     * Create a new QR code with URL and persist it to database
     * 
     * @param request Request containing URL and user info
     * @return BaseResponse with QRCodeDetailResponse
     */
    @Transactional
    @CacheEvict(value = "qrcode", allEntries = true)
    public BaseResponse<QRCodeDetailResponse> createQRCode(CreateQRCodeRequest request) {
        try {
            // Generate unique short code
            String shortCode = generateUniqueShortCode();
            
            // Generate redirect URL
            String redirectUrl = baseUrl + "/api/qrcode/r/" + shortCode;
            
            // Create QR code entity (without image - will be generated on-demand)
            QRCode qrCode = QRCode.builder()
                    .shortCode(shortCode)
                    .originalUrl(request.getUrl())
                    .userId(request.getUserId())
                    .width(request.getWidthOrDefault())
                    .height(request.getHeightOrDefault())
                    .scanCount(0L)
                    .build();
            
            // Save to database
            QRCode savedQRCode = qrCodeRepository.save(qrCode);
            
            // Save to Valkey for fast access
            saveToValkey(savedQRCode);
            
            // Generate QR code image for the redirect URL (for response only)
            byte[] qrCodeImageBytes = generateQRCodeImage(redirectUrl, savedQRCode.getWidth(), savedQRCode.getHeight());
            String base64Image = Base64.getEncoder().encodeToString(qrCodeImageBytes);
            
            // Build response
            QRCodeDetailResponse response = mapToDetailResponse(savedQRCode, base64Image, redirectUrl);
            
            LogUtil.addInfo("QR code created successfully with short code: {}", shortCode);
            return BaseResponse.success("QR code created successfully", response);
            
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to create QR code: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to create QR code: " + e.getMessage());
        }
    }
    
    /**
     * Get all QR codes for a specific user
     * 
     * @param userId User ID
     * @return BaseResponse with list of QRCodeDetailResponse
     */
    @Cacheable(value = "qrcode", key = "'list:' + #userId", unless = "#result == null || #result.data == null")
    public BaseResponse<List<QRCodeDetailResponse>> getUserQRCodes(String userId) {
        try {
            List<QRCode> qrCodes = qrCodeRepository.findByUserId(userId);
            
            List<QRCodeDetailResponse> responses = qrCodes.stream()
                    .map(qrCode -> {
                        String redirectUrl = baseUrl + "/api/qrcode/r/" + qrCode.getShortCode();
                        try {
                            // Regenerate QR code image from stored metadata
                            byte[] qrCodeImage = generateQRCodeImage(redirectUrl, qrCode.getWidth(), qrCode.getHeight());
                            String base64Image = Base64.getEncoder().encodeToString(qrCodeImage);
                            return mapToDetailResponse(qrCode, base64Image, redirectUrl);
                        } catch (Exception e) {
                            LogUtil.wrongInfo("Failed to generate QR image for short code: {}", qrCode.getShortCode(), e);
                            return mapToDetailResponse(qrCode, null, redirectUrl);
                        }
                    })
                    .collect(Collectors.toList());
            
            LogUtil.addInfo("Retrieved {} QR codes for user: {}", responses.size(), userId);
            return BaseResponse.success("QR codes retrieved successfully", responses);
            
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to get user QR codes: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to get QR codes: " + e.getMessage());
        }
    }
    
    /**
     * Get a specific QR code by short code
     * 
     * @param shortCode Short code
     * @param userId User ID (for authorization)
     * @return BaseResponse with QRCodeDetailResponse
     */
    @Cacheable(value = "qrcode", key = "'detail:' + #shortCode + ':' + #userId", unless = "#result == null || #result.data == null")
    public BaseResponse<QRCodeDetailResponse> getQRCode(String shortCode, String userId) {
        try {
            QRCode qrCode = qrCodeRepository.findByUserIdAndShortCode(userId, shortCode)
                    .orElse(null);
            
            if (qrCode == null) {
                return BaseResponse.error("QR code not found");
            }
            
            String redirectUrl = baseUrl + "/api/qrcode/r/" + qrCode.getShortCode();
            // Regenerate QR code image from stored metadata
            byte[] qrCodeImage = generateQRCodeImage(redirectUrl, qrCode.getWidth(), qrCode.getHeight());
            String base64Image = Base64.getEncoder().encodeToString(qrCodeImage);
            
            QRCodeDetailResponse response = mapToDetailResponse(qrCode, base64Image, redirectUrl);
            
            return BaseResponse.success("QR code retrieved successfully", response);
            
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to get QR code: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to get QR code: " + e.getMessage());
        }
    }
    
    /**
     * Delete a QR code
     * 
     * @param shortCode Short code
     * @param userId User ID (for authorization)
     * @return BaseResponse
     */
    @Transactional
    @CacheEvict(value = "qrcode", allEntries = true)
    public BaseResponse<Void> deleteQRCode(String shortCode, String userId) {
        try {
            QRCode qrCode = qrCodeRepository.findByUserIdAndShortCode(userId, shortCode)
                    .orElse(null);
            
            if (qrCode == null) {
                return BaseResponse.error("QR code not found");
            }
            
            // Delete from database
            qrCodeRepository.deleteByUserIdAndShortCode(userId, shortCode);
            
            // Delete from Valkey
            deleteFromValkey(shortCode);
            
            LogUtil.addInfo("QR code deleted: {}", shortCode);
            return BaseResponse.success("QR code deleted successfully", null);
            
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to delete QR code: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to delete QR code: " + e.getMessage());
        }
    }
    
    /**
     * Get original URL by short code and increment scan count
     * Tries Valkey first for fast lookup, falls back to database
     * 
     * @param shortCode Short code
     * @return Original URL or null if not found
     */
    @Transactional
    public String getOriginalUrlAndIncrementScan(String shortCode) {
        try {
            String originalUrl = null;
            
            // Try Valkey first for fast lookup
            Map<Object, Object> valkeyData = getFromValkey(shortCode);
            if (valkeyData != null && valkeyData.containsKey("originalUrl")) {
                originalUrl = (String) valkeyData.get("originalUrl");
                LogUtil.addInfo("QR code found in Valkey: {} -> {}", shortCode, originalUrl);
            } else {
                // Fallback to database
                QRCode qrCode = qrCodeRepository.findByShortCode(shortCode)
                        .orElse(null);
                
                if (qrCode == null) {
                    return null;
                }
                
                originalUrl = qrCode.getOriginalUrl();
                
                // Cache in Valkey for next time
                saveToValkey(qrCode);
                LogUtil.addInfo("QR code loaded from DB and cached: {} -> {}", shortCode, originalUrl);
            }
            
            // Increment scan count in database (async would be better for performance)
            qrCodeRepository.incrementScanCount(shortCode);
            
            LogUtil.addInfo("QR code scanned: {} -> {}", shortCode, originalUrl);
            return originalUrl;
            
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to get original URL: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Get original URL by short code WITHOUT incrementing scan count
     * This is used for testing the redirect endpoint
     * 
     * @param shortCode Short code
     * @return Original URL or null if not found
     */
    @Cacheable(value = "qrcode", key = "'redirect:' + #shortCode", unless = "#result == null")
    public String getOriginalUrlWithoutIncrement(String shortCode) {
        try {
            QRCode qrCode = qrCodeRepository.findByShortCode(shortCode)
                    .orElse(null);
            
            if (qrCode == null) {
                return null;
            }
            
            LogUtil.addInfo("Testing redirect for: {} -> {}", shortCode, qrCode.getOriginalUrl());
            return qrCode.getOriginalUrl();
            
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to get original URL: {}", e.getMessage(), e);
            return null;
        }
    }
    
    // ==================== Helper Methods ====================
    
    /**
     * Save QR code data to Valkey for fast access
     * 
     * @param qrCode QRCode entity to save
     */
    private void saveToValkey(QRCode qrCode) {
        if (redisTemplate == null) {
            LogUtil.addInfo("Valkey not available, skipping cache");
            return;
        }
        
        try {
            String key = VALKEY_KEY_PREFIX + qrCode.getShortCode();
            
            Map<String, Object> qrData = new HashMap<>();
            qrData.put("shortCode", qrCode.getShortCode());
            qrData.put("originalUrl", qrCode.getOriginalUrl());
            qrData.put("userId", qrCode.getUserId());
            qrData.put("width", qrCode.getWidth());
            qrData.put("height", qrCode.getHeight());
            qrData.put("scanCount", qrCode.getScanCount());
            qrData.put("createdAt", qrCode.getCreatedAt() != null ? qrCode.getCreatedAt().toString() : null);
            
            redisTemplate.opsForHash().putAll(key, qrData);
            redisTemplate.expire(key, valkeyTtlSeconds, TimeUnit.SECONDS);
            
            LogUtil.addInfo("Saved QR code to Valkey: {} (TTL: {}s)", qrCode.getShortCode(), valkeyTtlSeconds);
        } catch (Exception e) {
            // Log error but don't fail the operation - Valkey is optional
            LogUtil.wrongInfo("Failed to save QR code to Valkey: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Get QR code data from Valkey
     * 
     * @param shortCode Short code
     * @return Map of QR code data or null if not found
     */
    private Map<Object, Object> getFromValkey(String shortCode) {
        if (redisTemplate == null) {
            return null;
        }
        
        try {
            String key = VALKEY_KEY_PREFIX + shortCode;
            Map<Object, Object> qrData = redisTemplate.opsForHash().entries(key);
            
            if (qrData != null && !qrData.isEmpty()) {
                LogUtil.addInfo("Retrieved QR code from Valkey: {}", shortCode);
                return qrData;
            }
            return null;
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to get QR code from Valkey: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Delete QR code data from Valkey
     * 
     * @param shortCode Short code
     */
    private void deleteFromValkey(String shortCode) {
        try {
            String key = VALKEY_KEY_PREFIX + shortCode;
            redisTemplate.delete(key);
            LogUtil.addInfo("Deleted QR code from Valkey: {}", shortCode);
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to delete QR code from Valkey: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Generate a unique short code for QR redirect
     * 
     * @return Unique short code
     */
    private String generateUniqueShortCode() {
        String shortCode;
        int attempts = 0;
        int maxAttempts = 10;
        
        do {
            shortCode = generateRandomShortCode();
            attempts++;
            
            if (attempts > maxAttempts) {
                throw new RuntimeException("Failed to generate unique short code after " + maxAttempts + " attempts");
            }
        } while (qrCodeRepository.existsByShortCode(shortCode));
        
        return shortCode;
    }
    
    /**
     * Generate a random short code
     * 
     * @return Random short code
     */
    private String generateRandomShortCode() {
        StringBuilder sb = new StringBuilder(shortCodeLength);
        for (int i = 0; i < shortCodeLength; i++) {
            int index = random.nextInt(SHORT_CODE_CHARS.length());
            sb.append(SHORT_CODE_CHARS.charAt(index));
        }
        return sb.toString();
    }
    
    /**
     * Map QRCode entity to QRCodeDetailResponse
     * 
     * @param qrCode QRCode entity
     * @param base64Image Base64 encoded image (can be null)
     * @param redirectUrl Redirect URL
     * @return QRCodeDetailResponse
     */
    private QRCodeDetailResponse mapToDetailResponse(QRCode qrCode, String base64Image, String redirectUrl) {
        return QRCodeDetailResponse.builder()
                .id(qrCode.getId())
                .shortCode(qrCode.getShortCode())
                .originalUrl(qrCode.getOriginalUrl())
                .qrCodeImage(base64Image)
                .userId(qrCode.getUserId())
                .width(qrCode.getWidth())
                .height(qrCode.getHeight())
                .scanCount(qrCode.getScanCount())
                .createdAt(qrCode.getCreatedAt() != null ? qrCode.getCreatedAt().toString() : null)
                .updatedAt(qrCode.getUpdatedAt() != null ? qrCode.getUpdatedAt().toString() : null)
                .lastScannedAt(qrCode.getLastScannedAt() != null ? qrCode.getLastScannedAt().toString() : null)
                .redirectUrl(redirectUrl)
                .build();
    }
}
