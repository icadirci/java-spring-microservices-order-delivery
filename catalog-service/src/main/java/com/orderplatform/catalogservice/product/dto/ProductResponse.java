package com.orderplatform.catalogservice.product.dto;

import com.orderplatform.catalogservice.product.entity.Product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID productId,
        String title,
        String image,
        BigDecimal price,
        int stock
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getImage(),
                product.getPrice(),
                product.getStock()
        );
    }
}
