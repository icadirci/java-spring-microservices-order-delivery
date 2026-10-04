package com.orderplatform.catalogservice.catalog.dto;

import java.util.List;
import java.util.UUID;

public record CatalogCategoryResponse(
        UUID id,
        String title,
        String slug,
        List<CatalogCategoryResponse> subCategories
) {
}
