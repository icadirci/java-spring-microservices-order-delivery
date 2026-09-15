package com.orderplatform.orderservice.order.dto.request;

import com.orderplatform.orderservice.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest (
        @NotNull OrderStatus status
){
}
