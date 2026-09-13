package com.orderplatform.orderservice.order.service;

import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.common.grpc.UserRequest;
import com.orderplatform.common.grpc.UserResponse;
import com.orderplatform.common.grpc.UserServiceGrpcNavGrpc;
import com.orderplatform.orderservice.client.UserClient;
import com.orderplatform.orderservice.order.entity.Order;
import com.orderplatform.orderservice.order.exception.OrderNotFoundException;
import com.orderplatform.orderservice.order.exception.OrderUserDisabledException;
import com.orderplatform.orderservice.order.exception.OrderUserNotFoundException;
import com.orderplatform.orderservice.order.repository.OrderRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    @GrpcClient("userGrpcClient")
    private UserServiceGrpcNavGrpc.UserServiceGrpcNavBlockingStub userGrpcStub;



    public Order create(Long userId, String address) {
        log.info("Creating order for userId={}", userId);
        UserRequest gRpcRequest = UserRequest.newBuilder()
                .setId(userId)
                .build();

        UserResponse userResponse = userGrpcStub.getUserById(gRpcRequest);

        if (!userResponse.getEnabled()) {
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
