package com.orderplatform.orderservice.order.service;

import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.orderservice.client.UserClient;
import com.orderplatform.orderservice.client.dto.UserResponse;
import com.orderplatform.orderservice.order.entity.Order;
import com.orderplatform.orderservice.order.exception.OrderNotFoundException;
import com.orderplatform.orderservice.order.exception.OrderUserDisabledException;
import com.orderplatform.orderservice.order.exception.OrderUserNotFoundException;
import com.orderplatform.orderservice.order.repository.OrderRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserClient userClient;

    public OrderService(OrderRepository orderRepository,  UserClient userClient) {
        this.orderRepository = orderRepository;
        this.userClient = userClient;
    }

    public Order create(Long userId, String address) {
        log.info("Creating order for userId={}", userId);
        ApiResponse<UserResponse> user;
        try {
            user = userClient.getUserById(userId);
        } catch (FeignException.NotFound ex) {
            throw new OrderUserNotFoundException(userId);
        }

        if (!user.data().enabled()) {
            throw new OrderUserDisabledException();
        }
        Order order = new Order(userId, address);
        Order savedOrder = orderRepository.save(order);

        log.info("Order created successfully. orderId={}, userId={}",
                savedOrder.getId(), userId);
        return orderRepository.save(order);
    }

    public Order getOrder(Long orderId, Long userId) {
        return orderRepository
                .findByIdAndUserId(orderId, userId)
                .orElseThrow(OrderNotFoundException::new);
    }


}
