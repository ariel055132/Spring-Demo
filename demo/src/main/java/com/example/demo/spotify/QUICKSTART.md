# Spotify Playback System - Quick Start Guide

## 🚀 Quick Start

### 1. Start the Application

Ensure SSH tunnels are running for PostgreSQL:
```bash
cd /Users/adrianli/Documents/GitHub/Spring-Demo/demo
bash start-tunnels.sh

# Then start the application
./mvnw spring-boot:run
```

### 2. Verify Application Started

Check the logs for:
```
Started DemoApplication in X seconds
```

### 3. Access API Documentation

Open Swagger UI in your browser:
```
http://localhost:8081/swagger-ui.html
```

Look for the "Spotify" tag to see all available endpoints.

### 4. Test the Complete Data Flow

Use curl or any API client to test the endpoints (see examples below)

## 📡 API Endpoints

### Core Data Flow Endpoints

#### Trigger Playback
```bash
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=1&userId=user123"
```

#### Get Leaderboard
```bash
curl "http://localhost:8081/api/spotify/leaderboard?limit=10"
```

### Additional Endpoints

#### Create Track
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

#### Get All Tracks
```bash
curl "http://localhost:8081/api/spotify/tracks"
```

#### Get Track by ID
```bash
curl "http://localhost:8081/api/spotify/tracks/1"
```

#### Get User Playback History
```bash
curl "http://localhost:8081/api/spotify/playback/history/user123"
```

#### Complete Playback (Optional)
```bash
curl -X PUT "http://localhost:8081/api/spotify/playback/1/complete?durationPlayedSeconds=180"
```

## 📊 Data Flow Visualization

```
User clicks "Play" ────→ POST /playback/trigger
                              │
                              ├─→ Create Playback record
                              │
                              ├─→ Increment play_count
                              │
                              └─→ Return success
                                   │
                                   ▼
User refreshes ────────→ GET /leaderboard
                              │
                              └─→ Query ORDER BY play_count DESC
                                   │
                                   └─→ Shows updated rankings!
```

## 🎯 Testing Scenarios

### Scenario 1: Single User Multiple Plays
```bash
# Play track 1 three times
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=1&userId=alice"
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=1&userId=alice"
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=1&userId=alice"

# Check leaderboard
curl "http://localhost:8081/api/spotify/leaderboard?limit=5"
# Track 1 should have playCount = 3
```

### Scenario 2: Multiple Users
```bash
# Different users play different tracks
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=1&userId=alice"
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=1&userId=bob"
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=2&userId=charlie"
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=2&userId=dave"
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=2&userId=eve"

# Check leaderboard
curl "http://localhost:8081/api/spotify/leaderboard?limit=10"
# Track 2 should be #1 with 3 plays
# Track 1 should be #2 with 2 plays
```

### Scenario 3: Verify User History
```bash
# Alice plays multiple tracks
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=1&userId=alice"
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=3&userId=alice"
curl -X POST "http://localhost:8081/api/spotify/playback/trigger?trackId=5&userId=alice"

# Check Alice's history
curl "http://localhost:8081/api/spotify/playback/history/alice"
# Should return 3 playback records
```

## 🔍 Database Verification

Connect to PostgreSQL and verify the data:

```sql
-- Check track play counts
SELECT id, title, artist, play_count 
FROM tracks 
ORDER BY play_count DESC;

-- Check playback records
SELECT id, track_id, user_id, played_at, completed 
FROM playbacks 
ORDER BY played_at DESC 
LIMIT 10;

-- Verify data consistency
SELECT 
    t.id,
    t.title,
    t.play_count as stored_count,
    COUNT(p.id) as actual_playbacks
FROM tracks t
LEFT JOIN playbacks p ON t.id = p.track_id
GROUP BY t.id, t.title, t.play_count
HAVING t.play_count != COUNT(p.id);
-- Should return 0 rows (perfect consistency!)
```

## 📁 Project Structure

```
demo/src/main/java/com/example/demo/spotify/
├── controller/
│   ├── SpotifyController.java       # REST API endpoints
│   └── SpotifyViewController.java   # Web UI controller
├── entity/
│   ├── Track.java                   # Track model
│   └── Playback.java                # Playback event model
├── repository/
│   ├── TrackRepository.java         # Track data access
│   └── PlaybackRepository.java      # Playback data access
├── service/
│   └── SpotifyService.java          # Business logic (CORE!)
└── README.md                        # Full documentation

demo/src/main/resources/
├── db/migration/
│   └── V3__create_spotify_tables.sql  # Database schema
└── templates/
    └── spotify.html                    # Web UI
```

## 🎨 Web UI Features

- **Real-time updates**: See play counts change as you play
- **Leaderboard rankings**: Visual ranking with gold/silver/bronze medals
- **User tracking**: Each playback linked to user ID
- **Responsive design**: Works on desktop and mobile
- **Instant feedback**: Success/error messages for all actions

## 🐛 Troubleshooting

### Port 8081 not responding
```bash
# Check if application is running
ps aux | grep spring-boot:run

# Check if port is in use
lsof -i :8081

# Restart application
./mvnw spring-boot:run
```

### Database connection errors
```bash
# Check SSH tunnels
ps aux | grep "ssh -L"

# Restart tunnels
bash start-tunnels.sh
```

### Empty leaderboard
```bash
# Verify sample data loaded
curl "http://localhost:8081/api/spotify/tracks"

# If empty, check Flyway migrations
curl "http://localhost:8081/actuator/flyway"
```

## 📚 API Documentation

Full OpenAPI documentation available at:
```
http://localhost:8081/swagger-ui.html
```

Search for "Spotify" tag to see all endpoints.

## 🎯 Success Criteria

You'll know the system works when:
- ✅ Web UI loads at http://localhost:8081/spotify
- ✅ You can see 10 pre-loaded tracks
- ✅ Clicking "Play" shows success message
- ✅ Application starts successfully
- ✅ Swagger UI shows Spotify endpoints
- ✅ GET /api/spotify/tracks returns 10 pre-loaded tracks
- ✅ POST /api/spotify/playback/trigger creates playback and increments count
- ✅ GET /api/spotify/leaderboard shows tracks ordered by play count
- ✅ Play counts persist in database
- ✅ User history tracks all playbacks

Test using curl, Postman, or Swagger UI