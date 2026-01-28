package com.orderplatform.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class GatewayHeaderAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String userId = request.getHeader("X-Auth-UserId");
        String email = request.getHeader("X-Auth-Email");
        String role = request.getHeader("X-Auth-Role");

        if (userId != null && !userId.isBlank()) {
            List<GrantedAuthority> authorities = (role != null && !role.isBlank())
                    ? List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    : List.of();

            var auth = new UsernamePasswordAuthenticationToken(
                    email != null ? email : userId, // principal
                    "N/A", // placeholder credentials
                    authorities
            );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}

