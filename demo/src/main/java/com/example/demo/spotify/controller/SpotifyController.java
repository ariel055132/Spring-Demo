package com.example.demo.spotify.controller;

import com.example.demo.spotify.entity.Playback;
import com.example.demo.spotify.entity.Track;
import com.example.demo.spotify.service.SpotifyService;
import com.example.foundation.api.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Spotify-like playback system
 * Handles complete data flow from playback to leaderboard
 */
@RestController
@RequestMapping("/api/spotify")
@RequiredArgsConstructor
@Tag(name = "Spotify", description = "Spotify-like playback and leaderboard APIs")
public class SpotifyController {
    
    private final SpotifyService spotifyService;
    
    /**
     * Trigger playback - Main endpoint for user to play a track
     * Data flow: User clicks play -> Playback created -> Play count incremented -> Leaderboard updated
     * 
     * @param trackId Track ID to play
     * @param userId User ID triggering playback
     * @return Playback record
     */
    @PostMapping("/playback/trigger")
    @Operation(summary = "Trigger track playback", 
               description = "User initiates playback of a track. This creates a playback record and increments the track's play count, which updates the leaderboard.")
    public BaseResponse<Playback> triggerPlayback(
            @Parameter(description = "Track ID to play", required = true)
            @RequestParam Long trackId,
            @Parameter(description = "User ID triggering playback", required = true)
            @RequestParam String userId) {
        
        return spotifyService.triggerPlayback(trackId, userId);
    }
    
    /**
     * Complete playback - Mark playback as completed
     * 
     * @param playbackId Playback ID
     * @param durationPlayedSeconds Duration played in seconds
     * @return Updated playback record
     */
    @PutMapping("/playback/{playbackId}/complete")
    @Operation(summary = "Complete playback", 
               description = "Mark a playback session as completed with duration")
    public BaseResponse<Playback> completePlayback(
            @Parameter(description = "Playback ID", required = true)
            @PathVariable Long playbackId,
            @Parameter(description = "Duration played in seconds")
            @RequestParam Integer durationPlayedSeconds) {
        
        return spotifyService.completePlayback(playbackId, durationPlayedSeconds);
    }
    
    /**
     * Get leaderboard - Top tracks by play count
     * This reflects all play counts from triggered playbacks
     * 
     * @param limit Number of tracks to return (default 10)
     * @return List of top tracks ordered by play count
     */
    @GetMapping("/leaderboard")
    @Operation(summary = "Get leaderboard", 
               description = "Retrieve top tracks by play count. This shows the most played tracks based on all user playbacks.")
    public BaseResponse<List<Track>> getLeaderboard(
            @Parameter(description = "Number of tracks to return", example = "10")
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        return spotifyService.getLeaderboard(limit);
    }
    
    /**
     * Create a new track
     * 
     * @param track Track to create
     * @return Created track
     */
    @PostMapping("/tracks")
    @Operation(summary = "Create track", 
               description = "Add a new track to the system")
    public BaseResponse<Track> createTrack(@RequestBody Track track) {
        return spotifyService.createTrack(track);
    }
    
    /**
     * Get all tracks
     * 
     * @return List of all tracks
     */
    @GetMapping("/tracks")
    @Operation(summary = "Get all tracks", 
               description = "Retrieve all available tracks")
    public BaseResponse<List<Track>> getAllTracks() {
        return spotifyService.getAllTracks();
    }
    
    /**
     * Get track by ID
     * 
     * @param trackId Track ID
     * @return Track details
     */
    @GetMapping("/tracks/{trackId}")
    @Operation(summary = "Get track by ID", 
               description = "Retrieve details of a specific track including current play count")
    public BaseResponse<Track> getTrackById(
            @Parameter(description = "Track ID", required = true)
            @PathVariable Long trackId) {
        
        return spotifyService.getTrackById(trackId);
    }
    
    /**
     * Get user's playback history
     * 
     * @param userId User ID
     * @return List of playback records for the user
     */
    @GetMapping("/playback/history/{userId}")
    @Operation(summary = "Get playback history", 
               description = "Retrieve playback history for a specific user")
    public BaseResponse<List<Playback>> getPlaybackHistory(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId) {
        
        return spotifyService.getUserPlaybackHistory(userId);
    }
}
