package com.orderplatform.orderservice.order.exception;

import com.orderplatform.common.exception.BaseException;
import com.orderplatform.common.exception.ErrorCode;

public class OrderUserNotFoundException extends BaseException {

    public OrderUserNotFoundException(Long userId) {
        super(ErrorCode.NOT_FOUND,"User not found for order. userId=" + userId);
    }
}