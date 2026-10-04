package com.orderplatform.orderservice.order.dto.response;

import com.orderplatform.common.enums.PaymentStatus;
import com.orderplatform.orderservice.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDetailResponse(
        UUID id,
        String orderNumber,
        OrderStatus status,
        PaymentStatus paymentStatus,
        List<OrderItemResponse> items,
        BigDecimal subtotal,
        BigDecimal shippingFee,
        BigDecimal total,
        String shippingAddress,
        List<OrderStatusHistoryResponse> statusHistory,
        ShipmentResponse shipment,
        Instant createdAt
) {
}
