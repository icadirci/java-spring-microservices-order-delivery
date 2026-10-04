package com.orderplatform.orderservice.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PlaceOrderRequest(
        @NotNull UUID addressId,
        @NotEmpty @Valid List<OrderItemRequest> items
) {
}
