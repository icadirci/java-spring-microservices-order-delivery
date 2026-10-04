package com.orderplatform.userservice.user.dto.response;

import com.orderplatform.userservice.user.entity.UserAddress;

import java.time.Instant;
import java.util.UUID;

public record AddressResponse(
        UUID id,
        String title,
        String recipientName,
        String phone,
        String city,
        String district,
        String addressLine,
        String postalCode,
        boolean isDefault,
        Instant createdAt
) {
    public static AddressResponse from(UserAddress address) {
        return new AddressResponse(address.getId(), address.getTitle(), address.getRecipientName(), address.getPhone(), address.getCity(), address.getDistrict(), address.getAddressLine(), address.getPostalCode(), address.isDefault(), address.getCreatedAt());
    }
}
