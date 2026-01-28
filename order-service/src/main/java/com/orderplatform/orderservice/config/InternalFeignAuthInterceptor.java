package com.orderplatform.orderservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class InternalFeignAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            var request = servletAttrs.getRequest();
            String userId = request.getHeader("X-Auth-UserId");
            String email = request.getHeader("X-Auth-Email");
            String role = request.getHeader("X-Auth-Role");

            if (userId != null) template.header("X-Auth-UserId", userId);
            if (email != null) template.header("X-Auth-Email", email);
            if (role != null) template.header("X-Auth-Role", role);
        }
    }
}