package com.orderplatform.userservice.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final GatewayHeaderAuthFilter gatewayHeaderAuthFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthEntryPoint customAuthEntryPoint;

    public SecurityConfig(
            GatewayHeaderAuthFilter gatewayHeaderAuthFilter,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            CustomAuthEntryPoint customAuthEntryPoint

    ) {
        this.gatewayHeaderAuthFilter = gatewayHeaderAuthFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthEntryPoint = customAuthEntryPoint;

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthEntryPoint)   // 🔴 401
                        .accessDeniedHandler(customAccessDeniedHandler)   // 403
                )//                )
                // Authenticate only via headers propagated by the gateway
                .addFilterBefore(gatewayHeaderAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @EnableMethodSecurity
    @Configuration
    public class MethodSecurityConfig {
    }
}