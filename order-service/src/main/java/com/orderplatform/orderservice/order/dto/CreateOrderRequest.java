package com.orderplatform.orderservice.order.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
        @NotBlank String address
) {
}
