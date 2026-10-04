package com.orderplatform.userservice.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank @Size(max = 50) String title,
        @NotBlank @Size(max = 100) String recipientName,
        @NotBlank @Size(max = 40) String phone,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 100) String district,
        @NotBlank @Size(max = 500) String addressLine,
        @Size(max = 20) String postalCode,
        boolean isDefault
) {

}
