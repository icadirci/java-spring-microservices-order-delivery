package com.orderplatform.userservice.auth.dto;

public record UserMeResponse(
        Long id,
        String email,
        String fullName
) {
}
