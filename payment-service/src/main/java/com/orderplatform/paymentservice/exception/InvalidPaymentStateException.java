package com.orderplatform.paymentservice.exception;

import com.orderplatform.common.enums.PaymentStatus;
import com.orderplatform.common.exception.BaseException;
import com.orderplatform.common.exception.ErrorCode;

public class InvalidPaymentStateException extends BaseException {
    public InvalidPaymentStateException(PaymentStatus current, PaymentStatus expected, PaymentStatus target){
        super(ErrorCode.CONFLICT, "Ödeme " + target + " durumuna geçirilemez: mevcut durum " + current + ", beklenen " + expected + ".");
    }
}
