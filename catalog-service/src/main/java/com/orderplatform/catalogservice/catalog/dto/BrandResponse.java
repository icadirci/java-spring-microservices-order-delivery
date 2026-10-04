package com.orderplatform.catalogservice.catalog.dto;

import java.util.UUID;

public record BrandResponse(
        UUID id,
        String name
) {
}
