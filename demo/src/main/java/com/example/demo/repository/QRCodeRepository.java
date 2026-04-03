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
     * Find QR code by short code
     */
    Optional<QRCode> findByShortCode(String shortCode);
    
    /**
     * Find all QR codes for a specific user
     */
    List<QRCode> findByUserId(String userId);
    
    /**
     * Find QR code by user ID and short code
     */
    Optional<QRCode> findByUserIdAndShortCode(String userId, String shortCode);
    
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
