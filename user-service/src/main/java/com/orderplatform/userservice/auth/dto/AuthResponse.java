package com.orderplatform.userservice.auth.dto;

public record AuthResponse(
        String accessToken,
        String tokenType
) {}