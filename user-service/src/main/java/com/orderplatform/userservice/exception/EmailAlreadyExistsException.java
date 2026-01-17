package com.orderplatform.userservice.exception;

import com.orderplatform.common.exception.BaseException;
import com.orderplatform.common.exception.ErrorCode;

public class EmailAlreadyExistsException extends BaseException {

    public EmailAlreadyExistsException() {
        super(ErrorCode.CONFLICT, "Email already exists");
    }
}
