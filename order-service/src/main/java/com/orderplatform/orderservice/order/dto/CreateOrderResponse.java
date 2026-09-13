package com.orderplatform.orderservice.order.dto;

import java.util.UUID;

public record CreateOrderResponse(
        UUID id,
        UUID userId,
        String address,
        String orderStatus
) {
}
