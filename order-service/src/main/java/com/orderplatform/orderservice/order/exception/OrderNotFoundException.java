package com.orderplatform.orderservice.order.exception;

import com.orderplatform.common.exception.BaseException;
import com.orderplatform.common.exception.ErrorCode;

public class OrderNotFoundException extends BaseException {
    public OrderNotFoundException() {
        super(ErrorCode.NOT_FOUND, "Order not found");
    }
}
