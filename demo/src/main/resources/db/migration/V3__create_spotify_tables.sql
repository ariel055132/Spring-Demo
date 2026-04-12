-- Flyway Migration: Create Spotify tables for playback tracking and leaderboard
-- Version: V3__create_spotify_tables.sql

-- Create tracks table
CREATE TABLE IF NOT EXISTS tracks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(255) NOT NULL,
    album VARCHAR(255),
    duration_seconds INTEGER,
    play_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create playbacks table
CREATE TABLE IF NOT EXISTS playbacks (
    id BIGSERIAL PRIMARY KEY,
    track_id BIGINT NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    played_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    duration_played_seconds INTEGER,
    completed BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_playback_track FOREIGN KEY (track_id) REFERENCES tracks(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_tracks_play_count ON tracks(play_count DESC);
CREATE INDEX IF NOT EXISTS idx_tracks_artist ON tracks(artist);
CREATE INDEX IF NOT EXISTS idx_tracks_title ON tracks(title);
CREATE INDEX IF NOT EXISTS idx_playbacks_track_id ON playbacks(track_id);
CREATE INDEX IF NOT EXISTS idx_playbacks_user_id ON playbacks(user_id);
CREATE INDEX IF NOT EXISTS idx_playbacks_played_at ON playbacks(played_at DESC);

-- Insert sample data for demonstration
INSERT INTO tracks (title, artist, album, duration_seconds, play_count) VALUES
('Bohemian Rhapsody', 'Queen', 'A Night at the Opera', 354, 0),
('Stairway to Heaven', 'Led Zeppelin', 'Led Zeppelin IV', 482, 0),
('Hotel California', 'Eagles', 'Hotel California', 391, 0),
('Imagine', 'John Lennon', 'Imagine', 183, 0),
('Smells Like Teen Spirit', 'Nirvana', 'Nevermind', 301, 0),
('Billie Jean', 'Michael Jackson', 'Thriller', 294, 0),
('Sweet Child O'' Mine', 'Guns N'' Roses', 'Appetite for Destruction', 356, 0),
('Yesterday', 'The Beatles', 'Help!', 123, 0),
('Purple Haze', 'Jimi Hendrix', 'Are You Experienced', 170, 0),
('Wonderwall', 'Oasis', '(What''s the Story) Morning Glory?', 258, 0);

COMMENT ON TABLE tracks IS 'Music tracks with play count for leaderboard';
COMMENT ON TABLE playbacks IS 'Individual playback events tracking user listening history';
COMMENT ON COLUMN tracks.play_count IS 'Total number of times this track has been played - used for leaderboard ranking';
COMMENT ON COLUMN playbacks.completed IS 'Whether the user completed the full playback';
