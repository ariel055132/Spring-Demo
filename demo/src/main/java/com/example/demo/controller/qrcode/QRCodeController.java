package com.example.demo.controller.qrcode;

import com.example.demo.controller.qrcode.converter.QRCodeRequestConverter;
import com.example.demo.controller.qrcode.dto.CreateQRCodeRequest;
import com.example.demo.controller.qrcode.dto.GenerateQRCodeRequest;
import com.example.demo.service.qrcode.QRCodeService;
import com.example.demo.service.qrcode.arg.DeleteQRCodeArg;
import com.example.demo.service.qrcode.arg.GenerateQRCodeArg;
import com.example.demo.service.qrcode.response.QRCodeDetailResponse;
import com.example.demo.service.qrcode.response.QRCodeResponse;
import com.example.foundation.api.BaseResponse;
import com.example.foundation.util.LogUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

/**
 * REST Controller for QR Code generation and management
 */
@RestController
@RequestMapping("/api/qrcode")
@Tag(name = "QR Code", description = "QR Code generation and management APIs")
public class QRCodeController {

    @Autowired
    private QRCodeService qrCodeService;
    
    @Autowired
    private QRCodeRequestConverter converter;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Generate QR code and return as PNG image
     * 
     * @param request QR code generation request
     * @return QR code image as PNG
     */
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
            
            // Convert to service argument
            GenerateQRCodeArg arg = converter.toGenerateArg(request);
            
            // Generate QR code (returns BaseResponse)
            BaseResponse<QRCodeResponse> response = qrCodeService.generateQRCode(arg);
            
            // Check response status
            if (!"SUCCESS".equals(response.getStatus()) || response.getData() == null) {
                LogUtil.wrongInfo("Failed to generate QR code: {}", response.getMessage());
                throw new RuntimeException(response.getMessage());
            }
            
            // Decode Base64 to bytes for image response
            byte[] qrCodeImage = Base64.getDecoder().decode(response.getData().getQrcode());
            
            // Set headers
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

    /**
     * Generate QR code and return as Base64 string in JSON response
     * 
     * @param request QR code generation request
     * @return JSON response with Base64-encoded QR code
     */
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
        
        // Convert to service argument
        GenerateQRCodeArg arg = converter.toGenerateArg(request);
        
        // Generate QR code
        return qrCodeService.generateQRCode(arg);
    }

    /**
     * Generate QR code from URL parameter (GET request)
     * 
     * @param content Content to encode
     * @param width QR code width (optional)
     * @param height QR code height (optional)
     * @return QR code image as PNG
     */
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
            
            // Create service argument directly
            GenerateQRCodeArg arg = GenerateQRCodeArg.builder()
                    .content(content)
                    .width(width)
                    .height(height)
                    .build();
            
            // Generate QR code
            BaseResponse<QRCodeResponse> response = qrCodeService.generateQRCode(arg);
            
            // Check response status
            if (!"SUCCESS".equals(response.getStatus()) || response.getData() == null) {
                LogUtil.wrongInfo("Failed to generate QR code: {}", response.getMessage());
                throw new RuntimeException(response.getMessage());
            }
            
            // Decode Base64 to bytes for image response
            byte[] qrCodeImage = Base64.getDecoder().decode(response.getData().getQrcode());
            
            // Set headers
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

    /**
     * Health check endpoint for QR code service
     * 
     * @return Service status
     */
    @GetMapping("/health")
    @Operation(summary = "QR code service health check", 
               description = "Check if QR code generation service is operational")
    public BaseResponse<Map<String, String>> healthCheck() {
        Map<String, String> healthData = new HashMap<>();
        healthData.put("service", "QR Code Generator");
        healthData.put("status", "UP");
        healthData.put("library", "ZXing 3.5.3");
        healthData.put("timestamp", LocalDateTime.now().toString());
        
        return BaseResponse.success(
            "QR code service is operational", 
            healthData
        );
    }
    
    // ==================== QR Code Management Endpoints ====================
    
    /**
     * Create a new QR code with URL
     * 
     * @param request Create QR code request
     * @return QR code details with image
     */
    @PostMapping("/create")
    @Operation(summary = "Create QR code with URL", 
               description = "Creates a new QR code for a URL (ASCII, max 20 chars) and saves it to database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "QR code created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public BaseResponse<QRCodeDetailResponse> createQRCode(
            @Valid @RequestBody CreateQRCodeRequest request) {
        LogUtil.addInfo("Creating QR code for URL: {}, user: {}", request.getUrl(), request.getUserId());
        return qrCodeService.createQRCode(request);
    }
    
    /**
     * Get all QR codes for a user
     * 
     * @param userId User ID
     * @return List of user's QR codes
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's QR codes", 
               description = "Retrieves all QR codes created by a specific user")
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
    
    /**
     * Get a specific QR code
     * 
     * @param shortCode Short code
     * @return QR code details
     */
    @GetMapping("/detail/{shortCode}")
    @Operation(summary = "Get QR code details", 
               description = "Retrieves details of a specific QR code")
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
    
    /**
     * Delete a QR code
     * 
     * @param shortCode Short code
     * @param userId User ID (for authorization)
     * @return Success response
     */
    @DeleteMapping("/delete/{shortCode}")
    @Operation(summary = "Delete QR code", 
               description = "Deletes a QR code created by the user")
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
    
    // ==================== QR Code Redirect Endpoint ====================
    
    /**
     * Test redirect endpoint - shows where the QR code would redirect without actually redirecting
     * This endpoint is for testing in Swagger UI (which doesn't handle redirects well)
     * 
     * @param shortCode Short code from the QR code
     * @return JSON with redirect information
     */
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
        
        // Get original URL without incrementing scan count
        String originalUrl = qrCodeService.getOriginalUrlWithoutIncrement(shortCode);
        
        if (originalUrl == null) {
            LogUtil.wrongInfo("QR code not found: {}", shortCode);
            return BaseResponse.error("QR code not found");
        }
        
        // Return redirect information as JSON
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
     * Redirect endpoint for QR code scanning
     * When a user scans the QR code, they are redirected to the original URL
     * 
     * NOTE: This endpoint performs an HTTP 302 redirect which cannot be tested properly in Swagger UI.
     * To test this endpoint:
     * 1. Use the /r/{shortCode}/test endpoint in Swagger to verify the redirect URL
     * 2. Copy the redirect URL and paste it into your browser
     * 3. Or use: curl -L http://localhost:8080/api/qrcode/r/{shortCode}
     * 
     * @param shortCode Short code from the QR code
     * @return Redirect to original URL
     */
    @GetMapping("/r/{shortCode}")
    @Operation(summary = "QR code redirect (use /test for Swagger testing)", 
               description = "Redirects to the original URL when QR code is scanned. WARNING: This endpoint returns HTTP 302 redirect which Swagger UI cannot handle properly. Use the /r/{shortCode}/test endpoint for testing in Swagger, or test this in a browser/curl.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Redirect to original URL"),
        @ApiResponse(responseCode = "404", description = "QR code not found")
    })
    public ResponseEntity<Void> redirectQRCode(
            @Parameter(description = "Short code from QR", required = true)
            @PathVariable String shortCode) {
        
        LogUtil.addInfo("QR code scanned: {}", shortCode);
        
        // Get original URL and increment scan count
        String originalUrl = qrCodeService.getOriginalUrlAndIncrementScan(shortCode);
        
        if (originalUrl == null) {
            LogUtil.wrongInfo("QR code not found: {}", shortCode);
            return ResponseEntity.notFound().build();
        }
        
        // Redirect to original URL
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", originalUrl);
        
        LogUtil.addInfo("Redirecting to: {}", originalUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
