package com.orderplatform.orderservice.order.exception;

import com.orderplatform.common.exception.BaseException;
import com.orderplatform.common.exception.ErrorCode;

public class OrderUserDisabledException extends BaseException {

    public OrderUserDisabledException() {
        super(ErrorCode.FORBIDDEN, "User is disabled. Order cannot be created.");
    }
}