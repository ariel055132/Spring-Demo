package com.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * API Versioning Filter
 * 
 * Supports two versioning strategies:
 * 1. Path-based: /api/v1/weather/read → routes to backend, removes /v1
 * 2. Header-based: X-API-Version: 1 → routes based on header value
 * 
 * Default version is used if no version is specified.
 */
@Slf4j
@Component
public class ApiVersioningFilter implements GlobalFilter, Ordered {

    @Value("${api.version.default}")
    private String defaultVersion;

    @Value("${api.version.header-name}")
    private String versionHeaderName;

    private static final String VERSION_PATH_PATTERN = "/api/v(\\d+)/";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Extract version from path (e.g., /api/v1/weather/read)
        String versionFromPath = extractVersionFromPath(path);
        
        // Extract version from header
        String versionFromHeader = request.getHeaders().getFirst(versionHeaderName);

        // Determine version (path takes precedence over header)
        String version = versionFromPath != null ? versionFromPath 
            : versionFromHeader != null ? versionFromHeader 
            : defaultVersion;

        log.debug("API version: {} for path: {}", version, path);

        // Store version in exchange attributes for potential use by other filters
        exchange.getAttributes().put("apiVersion", version);

        // If version is in path, rewrite to remove version prefix
        if (versionFromPath != null) {
            String newPath = path.replaceFirst(VERSION_PATH_PATTERN, "/api/");
            ServerHttpRequest mutatedRequest = request.mutate()
                .path(newPath)
                .header("X-API-Version", version)
                .build();
            
            log.debug("Path rewritten: {} -> {}", path, newPath);
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        // If version is from header or default, just pass it through
        if (versionFromHeader == null) {
            ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-API-Version", version)
                .build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        return chain.filter(exchange);
    }

    /**
     * Extract API version from path
     * Example: /api/v1/weather/read -> "v1"
     */
    private String extractVersionFromPath(String path) {
        if (path.matches(".*" + VERSION_PATH_PATTERN + ".*")) {
            int startIndex = path.indexOf("/v") + 1;
            int endIndex = path.indexOf("/", startIndex + 1);
            if (endIndex > startIndex) {
                return path.substring(startIndex, endIndex);
            }
        }
        return null;
    }

    @Override
    public int getOrder() {
        // Run before JWT authentication
        return -150;
    }
}
