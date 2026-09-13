package com.orderplatform.orderservice.order.dto;

import com.orderplatform.orderservice.order.entity.Order;

import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID userId,
        String address,
        String orderStatus
) {
    public static OrderResponse from(Order order){
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getAddress(),
                order.getStatus().name()
                );
    }
}
