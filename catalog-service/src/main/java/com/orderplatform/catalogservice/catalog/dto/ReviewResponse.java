package com.orderplatform.catalogservice.catalog.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID userId,
        BigDecimal rating,
        String comment,
        Instant createdAt
) {
}
