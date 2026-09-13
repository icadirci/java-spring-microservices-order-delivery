package com.orderplatform.orderservice.order.controller;

import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.orderservice.order.dto.CreateOrderRequest;
import com.orderplatform.orderservice.order.dto.CreateOrderResponse;
import com.orderplatform.orderservice.order.dto.OrderResponse;
import com.orderplatform.orderservice.order.entity.Order;
import com.orderplatform.orderservice.order.service.OrderService;
import jakarta.validation.Valid;
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

    @PostMapping("/create")
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

    @GetMapping("/all")
    public ApiResponse<List<OrderResponse>> getAllOrders(Authentication authentication){
        UUID userId = UUID.fromString(authentication.getName());

        return ApiResponse.ok(orderService.getAllOrders(userId));
    }

}
