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
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for generating and managing QR codes
 */
@Service
public class QRCodeService {

    @Autowired
    private QRCodeRepository qrCodeRepository;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    private static final String SHORT_CODE_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

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
    public BaseResponse<QRCodeDetailResponse> createQRCode(CreateQRCodeRequest request) {
        try {
            // Generate unique short code
            String shortCode = generateUniqueShortCode();
            
            // Generate redirect URL
            String redirectUrl = baseUrl + "/api/qrcode/r/" + shortCode;
            
            // Create QR code entity
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
            
            // Generate QR code image for the redirect URL
            byte[] qrCodeImage = generateQRCodeImage(redirectUrl, savedQRCode.getWidth(), savedQRCode.getHeight());
            String base64Image = Base64.getEncoder().encodeToString(qrCodeImage);
            
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
    public BaseResponse<List<QRCodeDetailResponse>> getUserQRCodes(String userId) {
        try {
            List<QRCode> qrCodes = qrCodeRepository.findByUserId(userId);
            
            List<QRCodeDetailResponse> responses = qrCodes.stream()
                    .map(qrCode -> {
                        String redirectUrl = baseUrl + "/api/qrcode/r/" + qrCode.getShortCode();
                        try {
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
    public BaseResponse<QRCodeDetailResponse> getQRCode(String shortCode, String userId) {
        try {
            QRCode qrCode = qrCodeRepository.findByUserIdAndShortCode(userId, shortCode)
                    .orElse(null);
            
            if (qrCode == null) {
                return BaseResponse.error("QR code not found");
            }
            
            String redirectUrl = baseUrl + "/api/qrcode/r/" + qrCode.getShortCode();
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
    public BaseResponse<Void> deleteQRCode(String shortCode, String userId) {
        try {
            QRCode qrCode = qrCodeRepository.findByUserIdAndShortCode(userId, shortCode)
                    .orElse(null);
            
            if (qrCode == null) {
                return BaseResponse.error("QR code not found");
            }
            
            qrCodeRepository.deleteByUserIdAndShortCode(userId, shortCode);
            
            LogUtil.addInfo("QR code deleted: {}", shortCode);
            return BaseResponse.success("QR code deleted successfully", null);
            
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to delete QR code: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to delete QR code: " + e.getMessage());
        }
    }
    
    /**
     * Get original URL by short code and increment scan count
     * 
     * @param shortCode Short code
     * @return Original URL or null if not found
     */
    public String getOriginalUrlAndIncrementScan(String shortCode) {
        try {
            QRCode qrCode = qrCodeRepository.findByShortCode(shortCode)
                    .orElse(null);
            
            if (qrCode == null) {
                return null;
            }
            
            // Increment scan count
            qrCodeRepository.incrementScanCount(shortCode);
            
            LogUtil.addInfo("QR code scanned: {} -> {}", shortCode, qrCode.getOriginalUrl());
            return qrCode.getOriginalUrl();
            
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to get original URL: {}", e.getMessage(), e);
            return null;
        }
    }
    
    // ==================== Helper Methods ====================
    
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
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
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
                .createdAt(qrCode.getCreatedAt())
                .updatedAt(qrCode.getUpdatedAt())
                .lastScannedAt(qrCode.getLastScannedAt())
                .redirectUrl(redirectUrl)
                .build();
    }
}
