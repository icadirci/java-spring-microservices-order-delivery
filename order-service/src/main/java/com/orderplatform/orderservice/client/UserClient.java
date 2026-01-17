package com.orderplatform.orderservice.client;

import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.orderservice.client.dto.UserResponse;
import com.orderplatform.orderservice.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service",
configuration = InternalFeignConfig.class)
public interface UserClient {

    @GetMapping("api/users/{id}")
    ApiResponse<UserResponse> getUserById(@PathVariable("id") Long id);
}