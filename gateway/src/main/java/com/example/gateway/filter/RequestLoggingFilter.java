package com.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Request Logging Filter
 * 
 * Logs incoming requests with key details:
 * - HTTP method
 * - Request path
 * - Client IP address
 * - Correlation ID
 * - User agent
 */
@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        String method = request.getMethod().toString();
        String path = request.getURI().getPath();
        String clientIp = request.getRemoteAddress() != null 
            ? request.getRemoteAddress().getAddress().getHostAddress() 
            : "unknown";
        String correlationId = request.getHeaders().getFirst("X-Correlation-Id");
        String userAgent = request.getHeaders().getFirst("User-Agent");

        log.info("Incoming request: method={}, path={}, client={}, correlationId={}, userAgent={}", 
            method, path, clientIp, correlationId, userAgent);

        // Store request time for response filter
        exchange.getAttributes().put("requestTime", System.currentTimeMillis());

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Run after correlation ID filter
        return -190;
    }
}
