package com.orderplatform.catalogservice.catalog.dto;

import com.orderplatform.catalogservice.product.dto.ProductColor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CatalogProductResponse(
        UUID id,
        String title,
        String slug,
        String subTitle,
        String description,
        String brand,
        String model,
        UUID categoryId,
        String categoryTitle,
        BigDecimal price,
        BigDecimal oldPrice,
        BigDecimal rating,
        int reviewCount,
        String image,
        List<String> images,
        List<ProductColor> colors,
        Map<String, String> specs,
        boolean featured,
        int stock
) {
}
