package com.example.demo.spotify.repository;

import com.example.demo.spotify.entity.Playback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Playback entity
 */
@Repository
public interface PlaybackRepository extends JpaRepository<Playback, Long> {
    
    /**
     * Find all playbacks for a specific user
     */
    List<Playback> findByUserIdOrderByPlayedAtDesc(String userId);
    
    /**
     * Find all playbacks for a specific track
     */
    List<Playback> findByTrackIdOrderByPlayedAtDesc(Long trackId);
    
    /**
     * Find playbacks within a time range
     */
    @Query("SELECT p FROM Playback p WHERE p.playedAt >= :startTime AND p.playedAt <= :endTime ORDER BY p.playedAt DESC")
    List<Playback> findPlaybacksInRange(@Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime);
    
    /**
     * Count total playbacks for a user
     */
    long countByUserId(String userId);
    
    /**
     * Count total playbacks for a track
     */
    long countByTrackId(Long trackId);
}
