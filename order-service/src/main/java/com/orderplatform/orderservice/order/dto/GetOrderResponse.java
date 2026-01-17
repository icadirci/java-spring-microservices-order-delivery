package com.orderplatform.orderservice.order.dto;

public record GetOrderResponse(
        Long id,
        Long userId,
        String address,
        String orderStatus
) {
}
