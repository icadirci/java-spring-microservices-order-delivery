package com.orderplatform.userservice.exception;

import com.orderplatform.common.exception.BaseException;
import com.orderplatform.common.exception.ErrorCode;

public class InvalidCredentialsException extends BaseException {
    public InvalidCredentialsException() {
        super(ErrorCode.UNAUTHORIZED, "Invalid credentials");
    }
}
