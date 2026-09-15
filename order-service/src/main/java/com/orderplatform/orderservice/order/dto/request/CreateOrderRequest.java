package com.orderplatform.orderservice.order.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
        @NotBlank String address
) {
}
