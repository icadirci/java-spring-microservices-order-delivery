package com.orderplatform.userservice.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank @Size(max = 80) String fullName,
        @Size(max = 40) String phone,
        @Size(max = 255) String avatarUrl
) {
}
