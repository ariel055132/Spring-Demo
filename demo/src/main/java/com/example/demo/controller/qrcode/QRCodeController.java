package com.example.demo.controller.qrcode;

import com.example.demo.controller.qrcode.converter.QRCodeRequestConverter;
import com.example.demo.controller.qrcode.dto.CreateQRCodeRequest;
import com.example.demo.controller.qrcode.dto.GenerateQRCodeRequest;
import com.example.demo.controller.qrcode.dto.UpdateQRCodeRequest;
import com.example.demo.service.qrcode.QRCodeService;
import com.example.demo.service.qrcode.arg.CreateQRCodeArg;
import com.example.demo.service.qrcode.arg.DeleteQRCodeArg;
import com.example.demo.service.qrcode.arg.GenerateQRCodeArg;
import com.example.demo.service.qrcode.arg.UpdateQRCodeArg;
import com.example.demo.service.qrcode.response.QRCodeDetailResponse;
import com.example.demo.service.qrcode.response.QRCodeResponse;
import com.example.demo.service.qrcode.response.RedirectResult;
import com.example.foundation.api.BaseResponse;
import com.example.foundation.util.LogUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/qrcode")
@Tag(name = "QR Code", description = "QR Code generation and management APIs")
public class QRCodeController {

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private QRCodeRequestConverter converter;

    @Value("${app.base-url:http://localhost:8081}")
    private String baseUrl;

    // ==================== On-the-fly QR Code Generation ====================

    @PostMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Generate QR code image",
               description = "Generates a QR code image in PNG format for the provided content")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "QR code generated successfully",
                    content = @Content(mediaType = "image/png")),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<byte[]> generateQRCode(@Valid @RequestBody GenerateQRCodeRequest request) {
        try {
            LogUtil.addInfo("Received QR code generation request for content length: {}",
                    request.getContent() != null ? request.getContent().length() : 0);

            GenerateQRCodeArg arg = converter.toGenerateArg(request);
            BaseResponse<QRCodeResponse> response = qrCodeService.generateQRCode(arg);

            if (!"SUCCESS".equals(response.getStatus()) || response.getData() == null) {
                LogUtil.wrongInfo("Failed to generate QR code: {}", response.getMessage());
                throw new RuntimeException(response.getMessage());
            }

            byte[] qrCodeImage = Base64.getDecoder().decode(response.getData().getQrcode());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeImage.length);
            headers.set("Content-Disposition", "inline; filename=\"qrcode.png\"");

            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);

        } catch (Exception e) {
            LogUtil.wrongInfo("Error generating QR code: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }

    @PostMapping("/generate/base64")
    @Operation(summary = "Generate QR code as Base64",
               description = "Generates a QR code and returns it as Base64-encoded string in JSON response")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "QR code generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public BaseResponse<QRCodeResponse> generateQRCodeBase64(
            @Valid @RequestBody GenerateQRCodeRequest request) {
        LogUtil.addInfo("Received QR code Base64 generation request for content length: {}",
                request.getContent() != null ? request.getContent().length() : 0);
        GenerateQRCodeArg arg = converter.toGenerateArg(request);
        return qrCodeService.generateQRCode(arg);
    }

    @GetMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Generate QR code from URL parameters",
               description = "Generates a QR code image in PNG format using URL parameters")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "QR code generated successfully",
                    content = @Content(mediaType = "image/png")),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<byte[]> generateQRCodeFromParams(
            @Parameter(description = "Content to encode in QR code", required = true)
            @RequestParam String content,
            @Parameter(description = "QR code width in pixels (100-1000)", example = "300")
            @RequestParam(required = false, defaultValue = "300") int width,
            @Parameter(description = "QR code height in pixels (100-1000)", example = "300")
            @RequestParam(required = false, defaultValue = "300") int height) {
        try {
            LogUtil.addInfo("Received QR code generation request from URL params, content length: {}",
                    content != null ? content.length() : 0);

            GenerateQRCodeArg arg = GenerateQRCodeArg.builder()
                    .content(content).width(width).height(height).build();

            BaseResponse<QRCodeResponse> response = qrCodeService.generateQRCode(arg);

            if (!"SUCCESS".equals(response.getStatus()) || response.getData() == null) {
                LogUtil.wrongInfo("Failed to generate QR code: {}", response.getMessage());
                throw new RuntimeException(response.getMessage());
            }

            byte[] qrCodeImage = Base64.getDecoder().decode(response.getData().getQrcode());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeImage.length);
            headers.set("Content-Disposition", "inline; filename=\"qrcode.png\"");

            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);

        } catch (Exception e) {
            LogUtil.wrongInfo("Error generating QR code: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }

    @GetMapping("/health")
    @Operation(summary = "QR code service health check",
               description = "Check if QR code generation service is operational")
    public BaseResponse<Map<String, String>> healthCheck() {
        Map<String, String> healthData = new HashMap<>();
        healthData.put("service", "QR Code Generator");
        healthData.put("status", "UP");
        healthData.put("library", "ZXing 3.5.3");
        healthData.put("timestamp", LocalDateTime.now().toString());
        return BaseResponse.success("QR code service is operational", healthData);
    }

    // ==================== QR Code Management ====================

    @PostMapping("/create")
    @Operation(summary = "Create QR code with URL",
               description = "Creates a QR code for a URL, validates/normalizes it, and stores it in the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "QR code created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public BaseResponse<QRCodeDetailResponse> createQRCode(
            @Valid @RequestBody CreateQRCodeRequest request) {
        CreateQRCodeArg arg = converter.toCreateArg(request);
        LogUtil.addInfo("Creating QR code for URL: {}, user: {}", arg.getUrl(), arg.getUserId());
        return qrCodeService.createQRCode(arg);
    }

    @PatchMapping("/{shortCode}")
    @Operation(summary = "Update QR code",
               description = "Update the target URL and/or expiration of an existing QR code. "
                       + "Cache is invalidated automatically. Matches PATCH /api/qr/{token} in the reference.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "QR code updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "QR code not found")
    })
    public BaseResponse<QRCodeDetailResponse> updateQRCode(
            @Parameter(description = "Short code", required = true)
            @PathVariable String shortCode,
            @Valid @RequestBody UpdateQRCodeRequest request) {
        UpdateQRCodeArg arg = converter.toUpdateArg(shortCode, request);
        LogUtil.addInfo("Updating QR code: {}", shortCode);
        return qrCodeService.updateQRCode(arg);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's QR codes",
               description = "Retrieves all active QR codes created by a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "QR codes retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public BaseResponse<List<QRCodeDetailResponse>> getUserQRCodes(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId) {
        LogUtil.addInfo("Getting QR codes for user: {}", userId);
        return qrCodeService.getUserQRCodes(userId);
    }

    @GetMapping("/detail/{shortCode}")
    @Operation(summary = "Get QR code details",
               description = "Retrieves details of a specific active QR code. Matches GET /api/qr/{token}.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "QR code retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "QR code not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public BaseResponse<QRCodeDetailResponse> getQRCode(
            @Parameter(description = "Short code", required = true)
            @PathVariable String shortCode) {
        LogUtil.addInfo("Getting QR code: {}", shortCode);
        return qrCodeService.getQRCode(shortCode);
    }

    @DeleteMapping("/delete/{shortCode}")
    @Operation(summary = "Delete QR code",
               description = "Soft-deletes a QR code. Redirects to this code will subsequently return 410 Gone.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "QR code deleted successfully"),
        @ApiResponse(responseCode = "404", description = "QR code not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public BaseResponse<Void> deleteQRCode(
            @Parameter(description = "Short code", required = true)
            @PathVariable String shortCode,
            @Parameter(description = "User ID", required = true)
            @RequestParam String userId) {
        LogUtil.addInfo("Deleting QR code: {} for user: {}", shortCode, userId);
        DeleteQRCodeArg arg = converter.toDeleteArg(shortCode, userId);
        return qrCodeService.deleteQRCode(arg);
    }

    // ==================== QR Code Image ====================

    @GetMapping(value = "/{shortCode}/image", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Get QR code PNG image",
               description = "Returns the PNG image for a stored QR code's redirect URL. "
                       + "Matches GET /api/qr/{token}/image in the reference.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "QR code image returned",
                    content = @Content(mediaType = "image/png")),
        @ApiResponse(responseCode = "404", description = "QR code not found")
    })
    public ResponseEntity<byte[]> getQRCodeImage(
            @Parameter(description = "Short code", required = true)
            @PathVariable String shortCode) {
        try {
            byte[] imageBytes = qrCodeService.getQRCodeImageBytes(shortCode);
            if (imageBytes == null) {
                return ResponseEntity.notFound().build();
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(imageBytes.length);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            LogUtil.wrongInfo("Error fetching QR image for {}: {}", shortCode, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== Analytics ====================

    @GetMapping("/{shortCode}/analytics")
    @Operation(summary = "Get QR code scan analytics",
               description = "Returns total scans and per-day breakdown. "
                       + "Matches GET /api/qr/{token}/analytics in the reference.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analytics returned"),
        @ApiResponse(responseCode = "404", description = "QR code not found")
    })
    public BaseResponse<Map<String, Object>> getAnalytics(
            @Parameter(description = "Short code", required = true)
            @PathVariable String shortCode) {
        LogUtil.addInfo("Getting analytics for QR code: {}", shortCode);
        return qrCodeService.getAnalytics(shortCode);
    }

    // ==================== Redirect ====================

    @GetMapping("/r/{shortCode}/test")
    @Operation(summary = "Test QR code redirect (no actual redirect)",
               description = "Returns redirect information as JSON without performing the redirect. Use this for testing in Swagger UI.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Redirect information returned"),
        @ApiResponse(responseCode = "404", description = "QR code not found")
    })
    public BaseResponse<Map<String, Object>> testRedirect(
            @Parameter(description = "Short code from QR", required = true)
            @PathVariable String shortCode) {
        LogUtil.addInfo("Testing redirect for QR code: {}", shortCode);

        String originalUrl = qrCodeService.getOriginalUrlWithoutIncrement(shortCode);

        if (originalUrl == null) {
            LogUtil.wrongInfo("QR code not found or expired: {}", shortCode);
            return BaseResponse.error("QR code not found");
        }

        Map<String, Object> redirectInfo = new HashMap<>();
        redirectInfo.put("shortCode", shortCode);
        redirectInfo.put("redirectUrl", originalUrl);
        redirectInfo.put("redirectType", "HTTP 302 (Found)");
        redirectInfo.put("note", "This is a test endpoint. The actual redirect happens at GET /api/qrcode/r/{shortCode}");
        redirectInfo.put("testInBrowser", baseUrl + "/api/qrcode/r/" + shortCode);

        LogUtil.addInfo("Would redirect to: {}", originalUrl);
        return BaseResponse.success("Redirect information retrieved", redirectInfo);
    }

    /**
     * Redirect endpoint — 302 for active codes, 404 for missing, 410 Gone for deleted/expired.
     * Matches the /r/{token} route in the qr_code_generator reference implementation.
     */
    @GetMapping("/r/{shortCode}")
    @Operation(summary = "QR code redirect",
               description = "Redirects (302) to the original URL when QR code is scanned. "
                       + "Returns 410 Gone for deleted or expired codes, 404 for non-existent tokens. "
                       + "Use /r/{shortCode}/test in Swagger UI.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Redirect to original URL"),
        @ApiResponse(responseCode = "404", description = "QR code not found"),
        @ApiResponse(responseCode = "410", description = "QR code deleted or expired")
    })
    public ResponseEntity<Void> redirectQRCode(
            @Parameter(description = "Short code from QR", required = true)
            @PathVariable String shortCode,
            HttpServletRequest request) {

        LogUtil.addInfo("QR code scanned: {}", shortCode);

        String userAgent = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();

        RedirectResult result = qrCodeService.resolveRedirect(shortCode, userAgent, ipAddress);

        return switch (result.status()) {
            case FOUND -> {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Location", result.url());
                LogUtil.addInfo("Redirecting {} -> {}", shortCode, result.url());
                yield new ResponseEntity<>(headers, HttpStatus.FOUND);
            }
            case GONE -> {
                LogUtil.addInfo("QR code gone (deleted or expired): {}", shortCode);
                yield ResponseEntity.status(HttpStatus.GONE).build();
            }
            case NOT_FOUND -> {
                LogUtil.wrongInfo("QR code not found: {}", shortCode);
                yield ResponseEntity.notFound().build();
            }
        };
    }
}
