package com.orderplatform.userservice.exception;

import com.orderplatform.common.exception.BaseException;
import com.orderplatform.common.exception.ErrorCode;

public class InvalidCurrentPasswordException extends BaseException {

    public InvalidCurrentPasswordException(){super(ErrorCode.BAD_REQUEST, "The current password is incorrect.");}
}
