package com.orderplatform.orderservice.client.dto;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        boolean enabled
) {}