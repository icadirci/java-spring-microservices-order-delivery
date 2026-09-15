package com.orderplatform.orderservice.order.service;

import com.orderplatform.common.grpc.UserRequest;
import com.orderplatform.common.grpc.UserResponse;
import com.orderplatform.common.grpc.UserServiceGrpcNavGrpc;
import com.orderplatform.orderservice.order.dto.response.OrderResponse;
import com.orderplatform.orderservice.order.entity.Order;
import com.orderplatform.orderservice.order.entity.OrderStatus;
import com.orderplatform.orderservice.order.exception.OrderNotFoundException;
import com.orderplatform.orderservice.order.exception.OrderUserDisabledException;
import com.orderplatform.orderservice.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    @GrpcClient("userGrpcClient")
    private UserServiceGrpcNavGrpc.UserServiceGrpcNavBlockingStub userGrpcStub;



    public Order create(UUID userId, String address) {
        log.info("Creating order for userId={}", userId);
        UserRequest gRpcRequest = UserRequest.newBuilder()
                .setId(userId.toString())
                .build();

        UserResponse userResponse = userGrpcStub.getUserById(gRpcRequest);

        if (!userResponse.getEnabled()) {
            throw new OrderUserDisabledException();
        }
        Order order = new Order(userId, address);
        Order savedOrder = orderRepository.save(order);

        log.info("Order created successfully. orderId={}, userId={}",
                savedOrder.getId(), userId);
        return savedOrder;
    }

    public Order getOrder(UUID orderId, UUID userId) {
        return orderRepository
                .findByIdAndUserId(orderId, userId)
                .orElseThrow(OrderNotFoundException::new);
    }

    public List<OrderResponse> getMyOrders(UUID userId){
        List<Order> orders = orderRepository.findAllByUserId(userId);
        return orders.stream()
                .map(OrderResponse::from)
                .toList();
    }

    public OrderStatus updateStatus(UUID orderId, OrderStatus status){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        return status;

    }


}
