package com.orderplatform.userservice.auth.dto;

import java.util.UUID;

public record UserMeResponse(
        UUID id,
        String email,
        String fullName
) {
}
