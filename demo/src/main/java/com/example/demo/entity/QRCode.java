package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity for storing QR code mappings
 */
@Entity
@Table(name = "qr_codes", indexes = {
    @Index(name = "idx_short_code", columnList = "short_code", unique = true),
    @Index(name = "idx_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QRCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Unique short code for the QR code (used in the redirect URL)
     */
    @Column(name = "short_code", length = 10, nullable = false, unique = true)
    private String shortCode;
    
    /**
     * Original URL that the QR code redirects to
     */
    @Column(name = "original_url", length = 20, nullable = false)
    private String originalUrl;
    
    /**
     * User ID who created this QR code
     */
    @Column(name = "user_id", length = 50)
    private String userId;
    
    /**
     * Width of the QR code image
     */
    @Column(name = "width")
    private Integer width;
    
    /**
     * Height of the QR code image
     */
    @Column(name = "height")
    private Integer height;
    
    /**
     * Number of times this QR code has been scanned
     */
    @Column(name = "scan_count")
    @Builder.Default
    private Long scanCount = 0L;
    
    /**
     * Creation timestamp
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Last updated timestamp
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Last scanned timestamp
     */
    @Column(name = "last_scanned_at")
    private LocalDateTime lastScannedAt;
    
    /**
     * Set creation timestamp before persist
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Update timestamp before update
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
