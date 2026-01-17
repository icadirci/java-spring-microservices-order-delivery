package com.orderplatform.orderservice.order.controller;

import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.orderservice.order.dto.CreateOrderRequest;
import com.orderplatform.orderservice.order.dto.CreateOrderResponse;
import com.orderplatform.orderservice.order.dto.GetOrderResponse;
import com.orderplatform.orderservice.order.entity.Order;
import com.orderplatform.orderservice.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
        Long userId = Long.valueOf(authentication.getName());

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
    public ApiResponse<GetOrderResponse> getOrder(
            @PathVariable Long id,
            Authentication authentication
    ){
        Long userId = Long.valueOf(authentication.getName());

        Order order = orderService.getOrder(id, userId);
        return ApiResponse.ok(
                new GetOrderResponse(
                        order.getId(),
                        order.getUserId(),
                        order.getAddress(),
                        order.getStatus().name()
                )
        );
    }
}
