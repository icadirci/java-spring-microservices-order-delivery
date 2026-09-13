package com.orderplatform.orderservice.order.dto;


import java.util.List;

public record OrdersResponse(
    List<OrderResponse> data
) {
}
