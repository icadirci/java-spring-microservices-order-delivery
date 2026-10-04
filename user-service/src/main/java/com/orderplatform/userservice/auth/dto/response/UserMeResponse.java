package com.orderplatform.userservice.auth.dto.response;

import com.orderplatform.common.security.Role;

import java.time.Instant;
import java.util.UUID;

public record UserMeResponse(
        UUID id,
        String email,
        String fullName,
        String avatarUrl,
        String phone,
        Role role,
        Instant createdAt

) {
}
