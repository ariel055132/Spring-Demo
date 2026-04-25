package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Records an individual QR code scan event for analytics.
 * Matches the ScanEvent model in the qr_code_generator reference implementation.
 */
@Entity
@Table(name = "scan_events", indexes = {
        @Index(name = "idx_scan_events_token_scanned", columnList = "token, scanned_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", length = 10, nullable = false)
    private String token;

    @Column(name = "scanned_at", nullable = false)
    private LocalDateTime scannedAt;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @PrePersist
    protected void onCreate() {
        if (scannedAt == null) {
            scannedAt = LocalDateTime.now();
        }
    }
}
