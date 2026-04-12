package com.example.demo.spotify.repository;

import com.example.demo.spotify.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Track entity
 */
@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    
    /**
     * Find tracks by artist
     */
    List<Track> findByArtistContainingIgnoreCase(String artist);
    
    /**
     * Find tracks by title
     */
    List<Track> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Get top tracks by play count for leaderboard
     * @param limit Maximum number of tracks to return
     * @return List of tracks ordered by play count descending
     */
    @Query("SELECT t FROM Track t ORDER BY t.playCount DESC")
    List<Track> findTopByPlayCount(int limit);
    
    /**
     * Increment play count for a track
     * @param trackId Track ID
     */
    @Modifying
    @Query("UPDATE Track t SET t.playCount = t.playCount + 1, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :trackId")
    void incrementPlayCount(@Param("trackId") Long trackId);
}
