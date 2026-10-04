package com.orderplatform.userservice.exception;

import com.orderplatform.common.exception.BaseException;
import com.orderplatform.common.exception.ErrorCode;

public class AddressNotFoundException extends BaseException {
    public AddressNotFoundException(){
        super(ErrorCode.NOT_FOUND, "Address not found.");
    }
}
