# Spotify Playback & Leaderboard System

## Complete Data Flow Architecture

This module implements a complete Spotify-like playback tracking system with a leaderboard functionality. The data flow from user-triggered playback to leaderboard appearance is implemented as follows:

## 📊 Complete Data Flow

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    USER TRIGGERS PLAYBACK                                │
│                    (Frontend/API Call)                                   │
└─────────────────────┬───────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 1: SpotifyController.triggerPlayback()                            │
│  - Receives: trackId, userId                                             │
│  - POST /api/spotify/playback/trigger?trackId=1&userId=user123          │
└─────────────────────┬───────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 2: SpotifyService.triggerPlayback()                               │
│  - Validates track exists in database                                    │
│  - Creates Playback record (tracks individual play event)                │
│  - Increments Track.playCount (@Transactional)                          │
└─────────────────────┬───────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 3: Database Updates (Atomic Transaction)                          │
│  ┌────────────────────────────────────────────────────────────────┐     │
│  │ INSERT INTO playbacks (track_id, user_id, played_at)          │     │
│  │ VALUES (1, 'user123', NOW());                                  │     │
│  └────────────────────────────────────────────────────────────────┘     │
│  ┌────────────────────────────────────────────────────────────────┐     │
│  │ UPDATE tracks SET play_count = play_count + 1                 │     │
│  │ WHERE id = 1;                                                  │     │
│  └────────────────────────────────────────────────────────────────┘     │
└─────────────────────┬───────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 4: Leaderboard Auto-Updates                                       │
│  - GET /api/spotify/leaderboard?limit=10                                │
│  - Queries: SELECT * FROM tracks ORDER BY play_count DESC LIMIT 10      │
│  - Returns updated rankings with new play counts                        │
└─────────────────────────────────────────────────────────────────────────┘
```

## 🎯 Key Components

### Entities

#### Track
- Represents a music track
- Contains: `title`, `artist`, `album`, `durationSeconds`, **`playCount`**
- `playCount` is the key metric for leaderboard ranking
- Location: `entity/Track.java`

#### Playback
- Represents a single playback event
- Tracks: `trackId`, `userId`, `playedAt`, `completed`
- Each playback creates one record
- Location: `entity/Playback.java`

### Repositories

#### TrackRepository
- `incrementPlayCount(trackId)`: Atomic increment operation
- `findTopByPlayCount(limit)`: Retrieves leaderboard data
- Location: `repository/TrackRepository.java`

#### PlaybackRepository
- `save(playback)`: Records individual play events
- `findByUserIdOrderByPlayedAtDesc(userId)`: User history
- Location: `repository/PlaybackRepository.java`

### Service Layer

#### SpotifyService
Main business logic with complete data flow:

**1. `triggerPlayback(trackId, userId)`** - **PRIMARY ENTRY POINT**
```java
@Transactional
public BaseResponse<Playback> triggerPlayback(Long trackId, String userId) {
    // 1. Validate track
    Track track = trackRepository.findById(trackId).orElseThrow(...);
    
    // 2. Create playback record
    Playback playback = playbackRepository.save(...);
    
    // 3. Increment play count (affects leaderboard)
    trackRepository.incrementPlayCount(trackId);
    
    // 4. Return confirmation
    return BaseResponse.success("Playback started successfully", playback);
}
```

**2. `getLeaderboard(limit)`** - **LEADERBOARD ENDPOINT**
```java
public BaseResponse<List<Track>> getLeaderboard(Integer limit) {
    List<Track> topTracks = trackRepository.findTopByPlayCount(limit);
    return BaseResponse.success(..., topTracks);
}
```

Location: `service/SpotifyService.java`

### Controller Layer

#### SpotifyController
RESTful API endpoints:

- **`POST /api/spotify/playback/trigger`** - Trigger playback
- **`GET /api/spotify/leaderboard`** - Get top tracks
- **`POST /api/spotify/tracks`** - Create new track
- **`GET /api/spotify/tracks`** - Get all tracks
- **`GET /api/spotify/tracks/{trackId}`** - Get track details
- **`GET /api/spotify/playback/history/{userId}`** - User playback history
- **`PUT /api/spotify/playback/{playbackId}/complete`** - Complete playback

Location: `controller/SpotifyController.java`

## 🚀 Usage Examples

### 1. User Triggers Playback

```bash
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=1&userId=user123"
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Playback started successfully",
  "data": {
    "id": 1,
    "trackId": 1,
    "userId": "user123",
    "playedAt": "2026-04-12T01:30:00",
    "completed": false
  },
  "timestamp": "2026-04-12T01:30:00"
}
```

**Behind the scenes:**
- Playback record created
- Track play_count incremented: 0 → 1
- Leaderboard automatically updated

### 2. View Leaderboard

```bash
curl "http://localhost:8081/api/spotify/leaderboard?limit=5"
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Retrieved top 5 tracks",
  "data": [
    {
      "id": 1,
      "title": "Bohemian Rhapsody",
      "artist": "Queen",
      "playCount": 1,
      "createdAt": "2026-04-12T00:00:00",
      "updatedAt": "2026-04-12T01:30:00"
    }
  ],
  "timestamp": "2026-04-12T01:30:01"
}
```

### 3. Multiple Playbacks Update Leaderboard

```bash
# User 1 plays track 1
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=1&userId=user1"

# User 2 plays track 1
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=1&userId=user2"

# User 3 plays track 2
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=2&userId=user3"

# Check leaderboard
curl "http://localhost:8081/api/spotify/leaderboard?limit=10"
```

**Result:**
- Track 1: playCount = 2 (ranked #1)
- Track 2: playCount = 1 (ranked #2)
- Leaderboard automatically reflects all plays

### 4. Get User's Playback History

```bash
curl "http://localhost:8081/api/spotify/playback/history/user123"
```

### 5. Create New Track

```bash
curl -X POST "http://localhost:8081/api/spotify/tracks" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Wonderwall",
    "artist": "Oasis",
    "album": "(What'\''s the Story) Morning Glory?",
    "durationSeconds": 258
  }'
```

## 🗄️ Database Schema

### Table: `tracks`
```sql
CREATE TABLE tracks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(255) NOT NULL,
    album VARCHAR(255),
    duration_seconds INTEGER,
    play_count BIGINT DEFAULT 0,  -- Key field for leaderboard
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_tracks_play_count ON tracks(play_count DESC);
```

### Table: `playbacks`
```sql
CREATE TABLE playbacks (
    id BIGSERIAL PRIMARY KEY,
    track_id BIGINT NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    played_at TIMESTAMP NOT NULL,
    duration_played_seconds INTEGER,
    completed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (track_id) REFERENCES tracks(id)
);

CREATE INDEX idx_playbacks_user_id ON playbacks(user_id);
CREATE INDEX idx_playbacks_track_id ON playbacks(track_id);
```

## ⚡ Transaction Flow Guarantees

The system uses `@Transactional` to ensure atomicity:

```java
@Transactional
public BaseResponse<Playback> triggerPlayback(Long trackId, String userId) {
    // All operations succeed or all fail together:
    // 1. Create playback record
    // 2. Increment play count
    // If any step fails, entire transaction rolls back
}
```

This guarantees:
- ✅ No orphaned playback records
- ✅ Play count always accurate
- ✅ Leaderboard always consistent
- ✅ No race conditions on concurrent plays

## 🔍 Testing the Complete Flow

### Step-by-Step Test:

1. **Check initial leaderboard:**
   ```bash
   curl "http://localhost:8081/api/spotify/leaderboard?limit=10"
   # All tracks have playCount: 0
   ```

2. **Trigger multiple playbacks:**
   ```bash
   curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=1&userId=alice"
   curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=1&userId=bob"
   curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=1&userId=charlie"
   curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=2&userId=alice"
   curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=2&userId=bob"
   ```

3. **Verify leaderboard updated:**
   ```bash
   curl "http://localhost:8081/api/spotify/leaderboard?limit=10"
   # Track 1 should show playCount: 3 (ranked #1)
   # Track 2 should show playCount: 2 (ranked #2)
   ```

4. **Verify playback records created:**
   ```bash
   curl "http://localhost:8081/api/spotify/playback/history/alice"
   # Should show 2 playback records (track 1 and track 2)
   ```

## 📍 API Endpoints Summary

| Method | Endpoint | Description | Data Flow Step |
|--------|----------|-------------|----------------|
| POST | `/api/spotify/playback/trigger` | Start playback | **TRIGGER** ✅ |
| PUT | `/api/spotify/playback/{id}/complete` | Complete playback | Optional |
| GET | `/api/spotify/leaderboard` | Get top tracks | **LEADERBOARD** 📊 |
| GET | `/api/spotify/tracks` | List all tracks | Browse |
| POST | `/api/spotify/tracks` | Create track | Admin |
| GET | `/api/spotify/tracks/{id}` | Get track details | View |
| GET | `/api/spotify/playback/history/{userId}` | User history | Analytics |

## 🔐 Tech Stack

- **Spring Boot 3.4.1** - Framework
- **Spring Data JPA** - ORM
- **PostgreSQL** - Database
- **Flyway** - Database migrations
- **Lombok** - Boilerplate reduction
- **Swagger/OpenAPI** - API documentation

## 📝 Migration Script

The database schema is created automatically via Flyway migration:
- **File**: `V3__create_spotify_tables.sql`
- **Location**: `src/main/resources/db/migration/`
- Includes sample data (10 classic tracks)

## ✨ Key Features

✅ **Atomic Transactions**: Play counts always accurate  
✅ **Real-time Leaderboard**: Instantly reflects new plays  
✅ **User History Tracking**: Complete playback audit trail  
✅ **RESTful API**: Standard HTTP methods  
✅ **OpenAPI Documentation**: Auto-generated Swagger UI  
✅ **Sample Data**: 10 pre-loaded tracks for testing  
✅ **Optimized Indexes**: Fast leaderboard queries  

## 🚀 Getting Started

1. Ensure PostgreSQL is running
2. Application will auto-run Flyway migrations
3. Access Swagger UI: `http://localhost:8081/swagger-ui.html`
4. Test the flow:
   - POST to `/api/spotify/playback/trigger`
   - GET from `/api/spotify/leaderboard`
   - See play counts update!

## 📚 Architecture Pattern

This implementation follows:
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic separation
- **RESTful API Design**: Resource-oriented endpoints
- **Transactional Boundaries**: Data consistency guarantees
- **Event Sourcing Lite**: Playback events stored
 and SSH tunnels are active
2. Start the application: `./mvnw spring-boot:run`
3. Application will auto-run Flyway migrations
4. Access Swagger UI: `http://localhost:8081/swagger-ui.html`
5. Test the flow using curl or Postman