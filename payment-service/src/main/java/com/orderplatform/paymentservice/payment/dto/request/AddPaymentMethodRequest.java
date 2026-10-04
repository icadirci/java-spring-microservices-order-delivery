package com.orderplatform.paymentservice.payment.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddPaymentMethodRequest(
        @NotBlank String cardHolderName,
        @NotBlank @Pattern(regexp = "\\d{13,19}") String cardNumber,
        @Min(1) @Max(12) int expiryMonth,
        @Min(2000) int expiryYear,
        @NotBlank @Pattern(regexp = "\\d{3,4}") String cvv,
        boolean makeDefault
) {
}
