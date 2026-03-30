package com.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Response Header Filter
 * 
 * Adds standard headers to all responses:
 * - X-Correlation-Id: Request correlation ID for tracing
 * - X-Response-Time: Request processing duration in milliseconds
 * - X-Gateway-Version: Gateway version identifier
 */
@Slf4j
@Component
public class ResponseHeaderFilter implements GlobalFilter, Ordered {

    private static final String GATEWAY_VERSION = "1.0.0";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            
            // Add correlation ID to response
            String correlationId = (String) exchange.getAttributes().get("X-Correlation-Id");
            if (correlationId != null) {
                response.getHeaders().add("X-Correlation-Id", correlationId);
            }

            // Calculate and add response time
            Long requestTime = (Long) exchange.getAttributes().get("requestTime");
            if (requestTime != null) {
                long responseTime = System.currentTimeMillis() - requestTime;
                response.getHeaders().add("X-Response-Time", responseTime + "ms");
            }

            // Add gateway version
            response.getHeaders().add("X-Gateway-Version", GATEWAY_VERSION);

            // Log response
            String path = exchange.getRequest().getURI().getPath();
            int statusCode = response.getStatusCode() != null ? response.getStatusCode().value() : 0;
            log.info("Response: path={}, status={}, correlationId={}", path, statusCode, correlationId);
        }));
    }

    @Override
    public int getOrder() {
        // Run last to ensure headers are added after all processing
        return Ordered.LOWEST_PRECEDENCE;
    }
}
