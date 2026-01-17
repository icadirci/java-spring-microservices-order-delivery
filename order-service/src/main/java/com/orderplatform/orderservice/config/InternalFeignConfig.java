package com.orderplatform.orderservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalFeignConfig {

    @Bean
    public RequestInterceptor internalAuthInterceptor() {
        return new InternalFeignAuthInterceptor();
    }
}