package com.orderplatform.gateway_service.security;

import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // JWT for routed requests is enforced by JwtAuthGlobalFilter.
        // Gateway's own actuator endpoints are not routed, so they are guarded here.
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .matchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
                        .matchers(EndpointRequest.toAnyEndpoint()).denyAll()
                        .anyExchange().permitAll()
                );
        return http.build();
    }
}
