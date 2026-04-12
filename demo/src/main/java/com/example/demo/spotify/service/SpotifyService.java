package com.example.demo.spotify.service;

import com.example.demo.spotify.entity.Playback;
import com.example.demo.spotify.entity.Track;
import com.example.demo.spotify.repository.PlaybackRepository;
import com.example.demo.spotify.repository.TrackRepository;
import com.example.foundation.api.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for handling Spotify playback logic
 * Implements complete data flow: user playback trigger -> play count increment -> leaderboard update
 */
@Service
@RequiredArgsConstructor
public class SpotifyService {
    
    private static final Logger logger = LoggerFactory.getLogger(SpotifyService.class);
    
    private final TrackRepository trackRepository;
    private final PlaybackRepository playbackRepository;
    
    /**
     * Trigger playback - Main entry point for user-triggered playback
     * This initiates the complete data flow:
     * 1. Validate track exists
     * 2. Create playback record
     * 3. Increment play count
     * 4. Update leaderboard (automatically via play count)
     * 
     * @param trackId Track ID to play
     * @param userId User ID triggering playback
     * @return BaseResponse with Playback data
     */
    @Transactional
    public BaseResponse<Playback> triggerPlayback(Long trackId, String userId) {
        try {
            logger.info("User {} triggering playback for track {}", userId, trackId);
            
            // Step 1: Validate track exists
            Track track = trackRepository.findById(trackId)
                    .orElseThrow(() -> new IllegalArgumentException("Track not found with ID: " + trackId));
            
            // Step 2: Create playback record
            Playback playback = Playback.builder()
                    .trackId(trackId)
                    .userId(userId)
                    .playedAt(LocalDateTime.now())
                    .completed(false)
                    .build();
            
            playback = playbackRepository.save(playback);
            logger.info("Created playback record ID: {}", playback.getId());
            
            // Step 3: Increment play count (affects leaderboard)
            trackRepository.incrementPlayCount(trackId);
            logger.info("Incremented play count for track {} to {}", trackId, track.getPlayCount() + 1);
            
            // Step 4: Leaderboard automatically updates based on play_count
            // No additional action needed - queries will reflect new counts
            logger.info("Playback flow completed successfully for track: {} by user: {}", trackId, userId);
            
            return BaseResponse.success("Playback started successfully", playback);
            
        } catch (IllegalArgumentException e) {
            logger.error("Validation error during playback: {}", e.getMessage());
            return BaseResponse.error(e.getMessage());
        } catch (Exception e) {
            logger.error("Error triggering playback for track {}: {}", trackId, e.getMessage(), e);
            return BaseResponse.error("Failed to trigger playback: " + e.getMessage());
        }
    }
    
    /**
     * Complete playback - Mark playback as completed
     * 
     * @param playbackId Playback ID
     * @param durationPlayedSeconds Duration played in seconds
     * @return BaseResponse
     */
    @Transactional
    public BaseResponse<Playback> completePlayback(Long playbackId, Integer durationPlayedSeconds) {
        try {
            Playback playback = playbackRepository.findById(playbackId)
                    .orElseThrow(() -> new IllegalArgumentException("Playback not found with ID: " + playbackId));
            
            playback.setCompleted(true);
            playback.setDurationPlayedSeconds(durationPlayedSeconds);
            playback = playbackRepository.save(playback);
            
            logger.info("Completed playback ID: {} after {} seconds", playbackId, durationPlayedSeconds);
            
            return BaseResponse.success("Playback completed", playback);
            
        } catch (Exception e) {
            logger.error("Error completing playback {}: {}", playbackId, e.getMessage(), e);
            return BaseResponse.error("Failed to complete playback: " + e.getMessage());
        }
    }
    
    /**
     * Get leaderboard - Top tracks by play count
     * 
     * @param limit Number of tracks to return (default 10)
     * @return BaseResponse with List of Tracks
     */
    public BaseResponse<List<Track>> getLeaderboard(Integer limit) {
        try {
            if (limit == null || limit <= 0) {
                limit = 10;
            }
            
            List<Track> topTracks = trackRepository.findTopByPlayCount(limit);
            logger.info("Retrieved top {} tracks for leaderboard", topTracks.size());
            
            return BaseResponse.success(
                    String.format("Retrieved top %d tracks", topTracks.size()), 
                    topTracks
            );
            
        } catch (Exception e) {
            logger.error("Error retrieving leaderboard: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to retrieve leaderboard: " + e.getMessage());
        }
    }
    
    /**
     * Create a new track
     * 
     * @param track Track to create
     * @return BaseResponse with created Track
     */
    @Transactional
    public BaseResponse<Track> createTrack(Track track) {
        try {
            if (track.getPlayCount() == null) {
                track.setPlayCount(0L);
            }
            
            Track savedTrack = trackRepository.save(track);
            logger.info("Created new track: {} by {}", savedTrack.getTitle(), savedTrack.getArtist());
            
            return BaseResponse.success("Track created successfully", savedTrack);
            
        } catch (Exception e) {
            logger.error("Error creating track: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to create track: " + e.getMessage());
        }
    }
    
    /**
     * Get all tracks
     * 
     * @return BaseResponse with List of all Tracks
     */
    public BaseResponse<List<Track>> getAllTracks() {
        try {
            List<Track> tracks = trackRepository.findAll();
            logger.info("Retrieved {} total tracks", tracks.size());
            
            return BaseResponse.success("Tracks retrieved successfully", tracks);
            
        } catch (Exception e) {
            logger.error("Error retrieving tracks: {}", e.getMessage(), e);
            return BaseResponse.error("Failed to retrieve tracks: " + e.getMessage());
        }
    }
    
    /**
     * Get track by ID
     * 
     * @param trackId Track ID
     * @return BaseResponse with Track
     */
    public BaseResponse<Track> getTrackById(Long trackId) {
        try {
            Track track = trackRepository.findById(trackId)
                    .orElseThrow(() -> new IllegalArgumentException("Track not found with ID: " + trackId));
            
            return BaseResponse.success("Track retrieved successfully", track);
            
        } catch (IllegalArgumentException e) {
            return BaseResponse.error(e.getMessage());
        } catch (Exception e) {
            logger.error("Error retrieving track {}: {}", trackId, e.getMessage(), e);
            return BaseResponse.error("Failed to retrieve track: " + e.getMessage());
        }
    }
    
    /**
     * Get user's playback history
     * 
     * @param userId User ID
     * @return BaseResponse with List of Playbacks
     */
    public BaseResponse<List<Playback>> getUserPlaybackHistory(String userId) {
        try {
            List<Playback> playbacks = playbackRepository.findByUserIdOrderByPlayedAtDesc(userId);
            logger.info("Retrieved {} playback records for user {}", playbacks.size(), userId);
            
            return BaseResponse.success(
                    String.format("Retrieved %d playback records", playbacks.size()), 
                    playbacks
            );
            
        } catch (Exception e) {
            logger.error("Error retrieving playback history for user {}: {}", userId, e.getMessage(), e);
            return BaseResponse.error("Failed to retrieve playback history: " + e.getMessage());
        }
    }
}
