package com.orderplatform.orderservice.order.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID productId,
        String productTitle,
        String imageUrl,
        String variant,
        BigDecimal unitPrice,
        int quantity
) {
}
