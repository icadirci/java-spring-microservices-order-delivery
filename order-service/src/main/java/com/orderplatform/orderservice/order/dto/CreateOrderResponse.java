package com.orderplatform.orderservice.order.dto;

public record CreateOrderResponse(
        Long id,
        Long userId,
        String address,
        String orderStatus
) {
}