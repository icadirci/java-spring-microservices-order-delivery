package com.orderplatform.userservice.auth.dto;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        boolean enabled
) {
}
