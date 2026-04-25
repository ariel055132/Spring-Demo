package com.example.demo.repository;

import com.example.demo.entity.QRCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository for QRCode entity
 */
@Repository
public interface QRCodeRepository extends JpaRepository<QRCode, Long> {
    
    /**
     * Find QR code by short code regardless of deleted status (used to distinguish 404 vs 410)
     */
    Optional<QRCode> findByShortCode(String shortCode);

    /**
     * Find QR code by short code (active only)
     */
    Optional<QRCode> findByShortCodeAndIsDeletedFalse(String shortCode);

    /**
     * Find all active QR codes for a specific user
     */
    List<QRCode> findByUserIdAndIsDeletedFalse(String userId);

    /**
     * Find active QR code by user ID and short code
     */
    Optional<QRCode> findByUserIdAndShortCodeAndIsDeletedFalse(String userId, String shortCode);

    /**
     * Find active QR code by userId and originalURL
     */
    Optional<QRCode> findByUserIdAndOriginalUrlAndIsDeletedFalse(String userId, String originalUrl);

    /**
     * Check if a short code already exists
     */
    boolean existsByShortCode(String shortCode);
    
    /**
     * Delete QR code by short code and user ID
     */
    @Transactional
    void deleteByUserIdAndShortCode(String userId, String shortCode);
    
    /**
     * Increment scan count for a QR code
     */
    @Modifying
    @Transactional
    @Query("UPDATE QRCode q SET q.scanCount = q.scanCount + 1, q.lastScannedAt = CURRENT_TIMESTAMP WHERE q.shortCode = :shortCode")
    void incrementScanCount(@Param("shortCode") String shortCode);
    
    /**
     * Count QR codes for a specific user
     */
    long countByUserId(String userId);
}
