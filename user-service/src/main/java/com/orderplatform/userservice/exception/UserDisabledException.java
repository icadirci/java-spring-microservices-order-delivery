package com.orderplatform.userservice.exception;

import com.orderplatform.common.exception.BaseException;
import com.orderplatform.common.exception.ErrorCode;

public class UserDisabledException extends BaseException {
    public UserDisabledException(){
        super(ErrorCode.FORBIDDEN, "User account is disabled.");
    }
}
