package com.orderplatform.paymentservice.payment.dto.response;

import java.time.Instant;
import java.util.UUID;

public record PaymentMethodResponse(
        UUID id,
        String brand,
        String lastFour,
        boolean isDefault,
        Instant createdAt
) {
}
