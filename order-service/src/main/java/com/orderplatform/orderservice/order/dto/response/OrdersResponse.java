package com.orderplatform.orderservice.order.dto.response;


import java.util.List;

public record OrdersResponse(
    List<OrderResponse> data
) {
}
