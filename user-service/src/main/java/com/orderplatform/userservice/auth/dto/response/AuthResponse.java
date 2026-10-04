package com.orderplatform.userservice.auth.dto.response;

public record AuthResponse(
        String accessToken,
        String tokenType
) {}