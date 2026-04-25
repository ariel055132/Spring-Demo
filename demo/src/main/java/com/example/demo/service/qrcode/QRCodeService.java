package com.example.demo.service.qrcode;

import com.example.demo.entity.QRCode;
import com.example.demo.entity.ScanEvent;
import com.example.demo.repository.QRCodeRepository;
import com.example.demo.repository.ScanEventRepository;
import com.example.demo.service.qrcode.arg.CreateQRCodeArg;
import com.example.demo.service.qrcode.arg.DeleteQRCodeArg;
import com.example.demo.service.qrcode.arg.GenerateQRCodeArg;
import com.example.demo.service.qrcode.arg.UpdateQRCodeArg;
import com.example.demo.service.qrcode.checker.DeleteQRCodeChecker;
import com.example.demo.service.qrcode.converter.QRCodeConverter;
import com.example.demo.service.qrcode.response.QRCodeDetailResponse;
import com.example.demo.service.qrcode.response.QRCodeResponse;
import com.example.demo.service.qrcode.response.RedirectResult;
import com.example.demo.util.TokenGenerator;
import com.example.demo.util.UrlValidator;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class QRCodeService {

    @Autowired
    private QRCodeRepository qrCodeRepository;

    @Autowired
    private ScanEventRepository scanEventRepository;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${app.base-url:http://localhost:8081}")
    private String baseUrl;

    @Value("${app.qrcode.valkey-ttl:86400}")
    private long valkeyTtlSeconds;

    @Autowired
    private QRCodeConverter qrCodeConverter;

    private static final String VALKEY_KEY_PREFIX = "qrcode:";

    // ==================== QR Code Image Generation ====================

    /**
     * Generate QR code and return as Base64-encoded response.
     * Not persisted to database.
     *
     * @param arg Service argument containing QR code parameters
     * @return BaseResponse with QRCodeResponse data
     */
    public BaseResponse<QRCodeResponse> generateQRCode(GenerateQRCodeArg arg) {
        try {
            LogUtil.addInfo("Generating QR code for content length: {}, size: {}x{}",
                    arg.getContent().length(), arg.getWidth(), arg.getHeight());

            byte[] qrCodeImage = generateQRCodeImage(arg.getContent(), arg.getWidth(), arg.getHeight());
            String base64Image = Base64.getEncoder().encodeToString(qrCodeImage);
            QRCodeResponse response = qrCodeConverter.toQRCodeResponse(arg, qrCodeImage, base64Image);

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

    public byte[] generateQRCodeImage(GenerateQRCodeArg arg) throws WriterException, IOException {
        return generateQRCodeImage(arg.getContent(), arg.getWidth(), arg.getHeight());
    }

    private byte[] generateQRCodeImage(String content, int width, int height) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    // ==================== QR Code Management ====================

    /**
     * Create a new QR code.
     * URL is validated, normalized, and a SHA-256 + Base62 token is derived —
     * matching the qr_code_generator reference implementation.
     */
    @Transactional
    @CacheEvict(value = "qrcode", allEntries = true)
    public BaseResponse<QRCodeDetailResponse> createQRCode(CreateQRCodeArg arg) {
        try {
            // Validate and normalize URL (matches url_validator.py)
            String normalizedUrl;
            try {
                normalizedUrl = UrlValidator.validate(arg.getUrl());
            } catch (IllegalArgumentException e) {
                return BaseResponse.error(e.getMessage());
            }

            // Duplicate check against active records
            if (qrCodeRepository.findByUserIdAndOriginalUrlAndIsDeletedFalse(arg.getUserId(), normalizedUrl).isPresent()) {
                return BaseResponse.error("QR code already exists for this URL");
            }

            // SHA-256 + Base62 token (matches token_gen.py)
            String shortCode = TokenGenerator.generate(normalizedUrl, qrCodeRepository);

            QRCode qrCode = qrCodeConverter.createArgtoQRCode(arg, shortCode, normalizedUrl);
            QRCode savedQRCode = qrCodeRepository.save(qrCode);
            saveToValkey(savedQRCode);

            String redirectUrl = baseUrl + "/api/qrcode/r/" + shortCode;
            String qrCodeUrl = baseUrl + "/api/qrcode/" + shortCode + "/image";
            byte[] qrCodeImageBytes = generateQRCodeImage(redirectUrl, savedQRCode.getWidth(), savedQRCode.getHeight());
            String base64Image = Base64.getEncoder().encodeToString(qrCodeImageBytes);

            QRCodeDetailResponse response = qrCodeConverter.mapToDetailResponse(
                    savedQRCode, base64Image, redirectUrl, qrCodeUrl);

            LogUtil.addInfo("QR code created successfully with short code: {}", shortCode);
            return BaseResponse.success("QR code created successfully", response);

        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to create QR code: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to create QR code: " + e.getMessage());
        }
    }

    /**
     * Update target URL and/or expiration of an existing QR code.
     * Cache is invalidated on any change — matching the PATCH route in the reference.
     */
    @Transactional
    @CacheEvict(value = "qrcode", allEntries = true)
    public BaseResponse<QRCodeDetailResponse> updateQRCode(UpdateQRCodeArg arg) {
        try {
            QRCode qrCode = qrCodeRepository.findByShortCodeAndIsDeletedFalse(arg.getShortCode())
                    .orElse(null);

            if (qrCode == null) {
                return BaseResponse.error("QR code not found");
            }

            boolean cacheInvalidated = false;

            if (arg.getUrl() != null) {
                String normalized;
                try {
                    normalized = UrlValidator.validate(arg.getUrl());
                } catch (IllegalArgumentException e) {
                    return BaseResponse.error(e.getMessage());
                }
                qrCode.setOriginalUrl(normalized);
                cacheInvalidated = true;
            }

            if (arg.getExpiresAt() != null) {
                qrCode.setExpiresAt(arg.getExpiresAt());
                cacheInvalidated = true;
            }

            QRCode saved = qrCodeRepository.save(qrCode);
            if (cacheInvalidated) {
                deleteFromValkey(arg.getShortCode());
            }

            String redirectUrl = baseUrl + "/api/qrcode/r/" + saved.getShortCode();
            String qrCodeUrl = baseUrl + "/api/qrcode/" + saved.getShortCode() + "/image";
            byte[] qrCodeImageBytes = generateQRCodeImage(redirectUrl, saved.getWidth(), saved.getHeight());
            String base64Image = Base64.getEncoder().encodeToString(qrCodeImageBytes);

            QRCodeDetailResponse response = qrCodeConverter.mapToDetailResponse(
                    saved, base64Image, redirectUrl, qrCodeUrl);
            return BaseResponse.success("QR code updated successfully", response);

        } catch (IllegalArgumentException e) {
            return BaseResponse.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to update QR code: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to update QR code: " + e.getMessage());
        }
    }

    /**
     * Get all active QR codes for a user.
     */
    @Cacheable(value = "qrcode", key = "'list:' + #userId", unless = "#result == null || #result.data == null")
    public BaseResponse<List<QRCodeDetailResponse>> getUserQRCodes(String userId) {
        try {
            List<QRCode> qrCodes = qrCodeRepository.findByUserIdAndIsDeletedFalse(userId);

            List<QRCodeDetailResponse> responses = qrCodes.stream()
                    .map(qrCode -> {
                        String redirectUrl = baseUrl + "/api/qrcode/r/" + qrCode.getShortCode();
                        String qrCodeUrl = baseUrl + "/api/qrcode/" + qrCode.getShortCode() + "/image";
                        try {
                            byte[] qrCodeImage = generateQRCodeImage(redirectUrl, qrCode.getWidth(), qrCode.getHeight());
                            String base64Image = Base64.getEncoder().encodeToString(qrCodeImage);
                            return qrCodeConverter.mapToDetailResponse(qrCode, base64Image, redirectUrl, qrCodeUrl);
                        } catch (Exception e) {
                            LogUtil.wrongInfo("Failed to generate QR image for short code: {}", qrCode.getShortCode(), e);
                            return qrCodeConverter.mapToDetailResponse(qrCode, null, redirectUrl, qrCodeUrl);
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
     * Get details for a single active QR code by short code.
     */
    @Cacheable(value = "qrcode", key = "'detail:' + #shortCode", unless = "#result == null || #result.data == null")
    public BaseResponse<QRCodeDetailResponse> getQRCode(String shortCode) {
        try {
            QRCode qrCode = qrCodeRepository.findByShortCodeAndIsDeletedFalse(shortCode).orElse(null);

            if (qrCode == null) {
                return BaseResponse.error("QR code not found");
            }

            String redirectUrl = baseUrl + "/api/qrcode/r/" + qrCode.getShortCode();
            String qrCodeUrl = baseUrl + "/api/qrcode/" + qrCode.getShortCode() + "/image";
            byte[] qrCodeImage = generateQRCodeImage(redirectUrl, qrCode.getWidth(), qrCode.getHeight());
            String base64Image = Base64.getEncoder().encodeToString(qrCodeImage);

            QRCodeDetailResponse response = qrCodeConverter.mapToDetailResponse(
                    qrCode, base64Image, redirectUrl, qrCodeUrl);
            return BaseResponse.success("QR code retrieved successfully", response);

        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to get QR code: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to get QR code: " + e.getMessage());
        }
    }

    /**
     * Return PNG image bytes for the redirect QR of a stored short code.
     * Matches the /api/qr/{token}/image endpoint in the reference.
     */
    public byte[] getQRCodeImageBytes(String shortCode) throws IOException, WriterException {
        QRCode qrCode = qrCodeRepository.findByShortCodeAndIsDeletedFalse(shortCode).orElse(null);
        if (qrCode == null) {
            return null;
        }
        String redirectUrl = baseUrl + "/api/qrcode/r/" + shortCode;
        return generateQRCodeImage(redirectUrl, qrCode.getWidth(), qrCode.getHeight());
    }

    /**
     * Soft-delete a QR code. Redirects to this code will return 410 Gone.
     */
    @PreCheck(DeleteQRCodeChecker.class)
    @Transactional
    @CacheEvict(value = "qrcode", allEntries = true)
    public BaseResponse<Void> deleteQRCode(DeleteQRCodeArg arg) {
        try {
            qrCodeRepository.findByUserIdAndShortCodeAndIsDeletedFalse(arg.getUserId(), arg.getShortCode())
                    .ifPresent(qrCode -> {
                        qrCode.setDeleted(true);
                        qrCodeRepository.save(qrCode);
                    });
            deleteFromValkey(arg.getShortCode());

            LogUtil.addInfo("QR code soft-deleted: {}", arg.getShortCode());
            return BaseResponse.success("QR code deleted successfully", null);

        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to delete QR code: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to delete QR code: " + e.getMessage());
        }
    }

    // ==================== Redirect ====================

    /**
     * Resolve a redirect for the given short code, recording a ScanEvent.
     *
     * Cache-hit path  : Valkey → record scan → FOUND
     * Cache-miss path : DB → check deleted/expired → warm cache → record scan → FOUND | NOT_FOUND | GONE
     *
     * Matches the redirect flow from routes.py in the qr_code_generator reference.
     */
    @Transactional
    public RedirectResult resolveRedirect(String shortCode, String userAgent, String ipAddress) {
        try {
            // Fast path: Valkey cache
            Map<Object, Object> valkeyData = getFromValkey(shortCode);
            if (valkeyData != null && valkeyData.containsKey("originalUrl")) {
                String originalUrl = (String) valkeyData.get("originalUrl");
                recordScanEvent(shortCode, userAgent, ipAddress);
                qrCodeRepository.incrementScanCount(shortCode);
                LogUtil.addInfo("Redirect (cache hit): {} -> {}", shortCode, originalUrl);
                return RedirectResult.found(originalUrl);
            }

            // Cache miss — query DB (include deleted/expired to distinguish 404 vs 410)
            QRCode qrCode = qrCodeRepository.findByShortCode(shortCode).orElse(null);

            if (qrCode == null) {
                return RedirectResult.notFound();
            }

            if (qrCode.isDeleted() || isExpired(qrCode)) {
                return RedirectResult.gone();
            }

            // Warm cache and redirect
            saveToValkey(qrCode);
            recordScanEvent(shortCode, userAgent, ipAddress);
            qrCodeRepository.incrementScanCount(shortCode);
            LogUtil.addInfo("Redirect (db hit): {} -> {}", shortCode, qrCode.getOriginalUrl());
            return RedirectResult.found(qrCode.getOriginalUrl());

        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to resolve redirect: {}", e.getMessage(), e);
            return RedirectResult.notFound();
        }
    }

    // ==================== Analytics ====================

    /**
     * Return total and per-day scan counts for a short code.
     * Matches the /api/qr/{token}/analytics endpoint in the reference.
     */
    public BaseResponse<Map<String, Object>> getAnalytics(String shortCode) {
        try {
            QRCode qrCode = qrCodeRepository.findByShortCodeAndIsDeletedFalse(shortCode).orElse(null);
            if (qrCode == null) {
                return BaseResponse.error("QR code not found");
            }

            long total = scanEventRepository.countByToken(shortCode);
            List<Object[]> daily = scanEventRepository.countByDay(shortCode);

            List<Map<String, Object>> scansPerDay = new ArrayList<>();
            for (Object[] row : daily) {
                Map<String, Object> dayEntry = new HashMap<>();
                dayEntry.put("date", row[0].toString());
                dayEntry.put("count", row[1]);
                scansPerDay.add(dayEntry);
            }

            Map<String, Object> analytics = new HashMap<>();
            analytics.put("token", shortCode);
            analytics.put("total_scans", total);
            analytics.put("scans_by_day", scansPerDay);

            return BaseResponse.success("Analytics retrieved successfully", analytics);

        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to get analytics: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to get analytics: " + e.getMessage());
        }
    }

    // ==================== Test helper ====================

    @Cacheable(value = "qrcode", key = "'redirect:' + #shortCode", unless = "#result == null")
    public String getOriginalUrlWithoutIncrement(String shortCode) {
        try {
            QRCode qrCode = qrCodeRepository.findByShortCodeAndIsDeletedFalse(shortCode).orElse(null);
            if (qrCode == null || isExpired(qrCode)) {
                return null;
            }
            LogUtil.addInfo("Testing redirect for: {} -> {}", shortCode, qrCode.getOriginalUrl());
            return qrCode.getOriginalUrl();
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to get original URL: {}", e.getMessage(), e);
            return null;
        }
    }

    // ==================== Private helpers ====================

    private boolean isExpired(QRCode qrCode) {
        return qrCode.getExpiresAt() != null && qrCode.getExpiresAt().isBefore(LocalDateTime.now());
    }

    private void recordScanEvent(String token, String userAgent, String ipAddress) {
        try {
            ScanEvent event = ScanEvent.builder()
                    .token(token)
                    .scannedAt(LocalDateTime.now())
                    .userAgent(userAgent)
                    .ipAddress(ipAddress)
                    .build();
            scanEventRepository.save(event);
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to record scan event for token {}: {}", token, e.getMessage(), e);
        }
    }

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
            LogUtil.wrongInfo("Failed to save QR code to Valkey: {}", e.getMessage(), e);
        }
    }

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

    private void deleteFromValkey(String shortCode) {
        if (redisTemplate == null) {
            return;
        }
        try {
            String key = VALKEY_KEY_PREFIX + shortCode;
            redisTemplate.delete(key);
            LogUtil.addInfo("Deleted QR code from Valkey: {}", shortCode);
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to delete QR code from Valkey: {}", e.getMessage(), e);
        }
    }
}
