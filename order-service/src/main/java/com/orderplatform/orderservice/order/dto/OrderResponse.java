package com.orderplatform.orderservice.order.dto;

import com.orderplatform.orderservice.order.entity.Order;

public record OrderResponse(
        Long id,
        Long userId,
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
