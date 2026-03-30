package com.example.gateway.controller;

import com.example.gateway.model.AuthRequest;
import com.example.gateway.model.AuthResponse;
import com.example.gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller
 * 
 * Provides JWT token generation endpoint for testing.
 * In production, replace with proper OAuth2 authorization server.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Mock user database (replace with real user service in production)
    private static final Map<String, String> MOCK_USERS = new HashMap<>();
    
    static {
        // Password: "password" (BCrypt encoded)
        MOCK_USERS.put("admin", "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG");
        MOCK_USERS.put("user", "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG");
        MOCK_USERS.put("test", "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG");
    }

    /**
     * Generate JWT token
     * 
     * POST /auth/token
     * Body: {"username": "test", "password": "password"}
     * Response: {"token": "eyJhbGc...", "expiresIn": 3600000, "username": "test"}
     */
    @PostMapping("/token")
    public Mono<ResponseEntity<AuthResponse>> generateToken(@RequestBody AuthRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        log.info("Authentication request for user: {}", username);

        // Validate credentials
        if (username == null || password == null) {
            log.warn("Missing username or password");
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        }

        String storedPassword = MOCK_USERS.get(username);
        if (storedPassword == null || !passwordEncoder.matches(password, storedPassword)) {
            log.warn("Invalid credentials for user: {}", username);
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        // Generate token with custom claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");
        String token = jwtUtil.generateToken(username, claims);

        log.info("Token generated successfully for user: {}", username);

        AuthResponse response = AuthResponse.builder()
            .token(token)
            .expiresIn(3600000L)  // 1 hour
            .username(username)
            .build();

        return Mono.just(ResponseEntity.ok(response));
    }

    /**
     * Validate JWT token (for testing)
     * 
     * GET /auth/validate
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/validate")
    public Mono<ResponseEntity<Map<String, Object>>> validateToken(
            @RequestHeader("Authorization") String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        String token = authHeader.substring(7);
        
        try {
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", username);
                return Mono.just(ResponseEntity.ok(response));
            }
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
        }

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
