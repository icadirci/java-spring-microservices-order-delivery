package com.orderplatform.gateway_service.security;

import io.jsonwebtoken.Claims;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // Paths that do NOT require authentication
    private static final List<String> PUBLIC_PATTERNS = List.of(
            "/api/auth/**",
            "/actuator/**"
    );

    public JwtAuthGlobalFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (isPublic(path)) {
            return chain.filter(exchange);
        }

        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = header.substring(7);
        Claims claims;
        try {
            claims = jwtService.parse(token);
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Extract claims (subject = userId, optional email/role).
        String userId = claims.getSubject();
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        ServerHttpRequest mutated = exchange.getRequest()
                .mutate()
                .header("X-Auth-UserId", userId == null ? "" : userId)
                .header("X-Auth-Email", email == null ? "" : email)
                .header("X-Auth-Role", role == null ? "" : role)
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private boolean isPublic(String path) {
        for (String pattern : PUBLIC_PATTERNS) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        // High precedence, authenticate early
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

