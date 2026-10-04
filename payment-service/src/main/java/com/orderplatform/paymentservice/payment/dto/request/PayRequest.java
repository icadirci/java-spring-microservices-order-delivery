package com.orderplatform.paymentservice.payment.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PayRequest(
        @NotNull UUID orderId,
        @NotNull UUID paymentMethodId
) {
}
