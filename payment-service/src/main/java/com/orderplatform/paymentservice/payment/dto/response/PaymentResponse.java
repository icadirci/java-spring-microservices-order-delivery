package com.orderplatform.paymentservice.payment.dto.response;

import com.orderplatform.common.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        BigDecimal amount,
        String currency,
        PaymentStatus paymentStatus,
        String failureReason,
        Instant paidAt
) {
}
