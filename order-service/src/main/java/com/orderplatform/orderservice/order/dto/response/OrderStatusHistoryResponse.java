package com.orderplatform.orderservice.order.dto.response;

import com.orderplatform.orderservice.order.entity.OrderStatus;

import java.time.Instant;

public record OrderStatusHistoryResponse(
        OrderStatus status,
        String info,
        Instant createdAt
) {
}
