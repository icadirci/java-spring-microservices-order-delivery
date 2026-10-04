package com.orderplatform.orderservice.order.dto.response;

import java.time.Instant;

public record ShipmentResponse(
        String carrier,
        String trackingNumber,
        String trackingUrl,
        Instant estimatedDelivery
) {
}
