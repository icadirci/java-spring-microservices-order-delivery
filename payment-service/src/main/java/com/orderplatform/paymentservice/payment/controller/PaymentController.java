package com.orderplatform.paymentservice.payment.controller;

import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.paymentservice.payment.dto.request.AddPaymentMethodRequest;
import com.orderplatform.paymentservice.payment.dto.request.PayRequest;
import com.orderplatform.paymentservice.payment.dto.response.PaymentMethodResponse;
import com.orderplatform.paymentservice.payment.dto.response.PaymentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// TODO: Implement.
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @GetMapping("/methods")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<List<PaymentMethodResponse>> getMethods(Authentication authentication) {
        return null;
    }

    @PostMapping("/methods")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<PaymentMethodResponse> addMethod(@RequestBody @Valid AddPaymentMethodRequest request, Authentication authentication) {
        return null;
    }

    @PatchMapping("/methods/{id}/default")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<PaymentMethodResponse> setDefault(@PathVariable UUID id, Authentication authentication) {
        return null;
    }

    @DeleteMapping("/methods/{id}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<Void> deleteMethod(@PathVariable UUID id, Authentication authentication) {
        return null;
    }

    @PostMapping("/pay")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<PaymentResponse> pay(@RequestBody @Valid PayRequest request, Authentication authentication) {
        return null;
    }
}
