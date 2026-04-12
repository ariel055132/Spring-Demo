package com.example.demo.spotify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Playback entity representing a single playback event
 */
@Entity
@Table(name = "playbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Playback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "track_id", nullable = false)
    private Long trackId;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;
    
    @Column(name = "duration_played_seconds")
    private Integer durationPlayedSeconds;
    
    @Column(name = "completed")
    @Builder.Default
    private Boolean completed = false;
    
    @PrePersist
    protected void onCreate() {
        if (playedAt == null) {
            playedAt = LocalDateTime.now();
        }
        if (completed == null) {
            completed = false;
        }
    }
}
