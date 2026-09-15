package com.orderplatform.orderservice.order.controller;

import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.orderservice.order.dto.request.CreateOrderRequest;
import com.orderplatform.orderservice.order.dto.request.UpdateOrderStatusRequest;
import com.orderplatform.orderservice.order.dto.response.CreateOrderResponse;
import com.orderplatform.orderservice.order.dto.response.OrderResponse;
import com.orderplatform.orderservice.order.entity.Order;
import com.orderplatform.orderservice.order.entity.OrderStatus;
import com.orderplatform.orderservice.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<CreateOrderResponse> create(
            @RequestBody @Valid CreateOrderRequest request,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        Order order = orderService.create(userId, request.address());

        return ApiResponse.ok(
                new CreateOrderResponse(
                        order.getId(),
                        order.getUserId(),
                        order.getAddress(),
                        order.getStatus().name()
                )
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrder(
            @PathVariable UUID id,
            Authentication authentication
    ){
        UUID userId = UUID.fromString(authentication.getName());

        Order order = orderService.getOrder(id, userId);
        return ApiResponse.ok(
                new OrderResponse(
                        order.getId(),
                        order.getUserId(),
                        order.getAddress(),
                        order.getStatus().name()
                )
        );
    }


    @GetMapping
    public ApiResponse<List<OrderResponse>> getMyOrders(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ApiResponse.ok(orderService.getMyOrders(userId));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<OrderStatus> updateStatus(@PathVariable UUID orderId, @RequestBody @Valid UpdateOrderStatusRequest request) {
        return ApiResponse.ok(orderService.updateStatus(orderId, request.status()));
    }

}
