package com.orderplatform.gateway_service.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.common.security.AuthHeaders;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;

@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthGlobalFilter.class);

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER_PREFIX = AuthHeaders.PREFIX.toLowerCase(Locale.ROOT);

    // Deny by default: everything not listed here requires a valid token.
    private static final List<PublicEndpoint> PUBLIC_ENDPOINTS = List.of(
            new PublicEndpoint(HttpMethod.POST, "/api/auth/login"),
            new PublicEndpoint(HttpMethod.POST, "/api/auth/register"),
            new PublicEndpoint(HttpMethod.GET, "/actuator/health/**")
    );

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthGlobalFilter(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Never trust identity headers coming from the client, on any path.
        ServerHttpRequest sanitized = stripAuthHeaders(exchange.getRequest());

        if (isPublic(sanitized)) {
            return chain.filter(exchange.mutate().request(sanitized).build());
        }

        String header = sanitized.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return unauthorized(exchange, "Missing bearer token");
        }

        Claims claims;
        try {
            claims = jwtService.parse(header.substring(BEARER_PREFIX.length()));
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT rejected: {}", e.getMessage());
            return unauthorized(exchange, "Invalid or expired token");
        }

        String userId = claims.getSubject();
        if (!StringUtils.hasText(userId)) {
            return unauthorized(exchange, "Token has no subject");
        }

        ServerHttpRequest authenticated = sanitized.mutate()
                .headers(h -> {
                    h.set(AuthHeaders.USER_ID, userId);
                    setIfPresent(h, AuthHeaders.EMAIL, claims.get("email", String.class));
                    setIfPresent(h, AuthHeaders.ROLE, claims.get("role", String.class));
                })
                .build();

        return chain.filter(exchange.mutate().request(authenticated).build());
    }

    private ServerHttpRequest stripAuthHeaders(ServerHttpRequest request) {
        return request.mutate()
                .headers(h -> h.keySet().removeIf(name ->
                        name.toLowerCase(Locale.ROOT).startsWith(AUTH_HEADER_PREFIX)))
                .build();
    }

    private boolean isPublic(ServerHttpRequest request) {
        String path = request.getPath().pathWithinApplication().value();
        return PUBLIC_ENDPOINTS.stream().anyMatch(e ->
                e.method().equals(request.getMethod()) && pathMatcher.match(e.pattern(), path));
    }

    private static void setIfPresent(HttpHeaders headers, String name, String value) {
        if (StringUtils.hasText(value)) {
            headers.set(name, value);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] body;
        try {
            body = objectMapper.writeValueAsBytes(ApiResponse.fail(message));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
        DataBuffer buffer = response.bufferFactory().wrap(body);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        // Authenticate before any routing filter runs.
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private record PublicEndpoint(HttpMethod method, String pattern) {
    }
}
