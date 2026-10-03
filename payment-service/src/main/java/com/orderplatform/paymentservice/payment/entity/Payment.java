package com.orderplatform.paymentservice.payment.entity;

import com.orderplatform.common.enums.PaymentStatus;
import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "ix_payments_order_id", columnList = "order_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Payment extends UuidV7Entity {
    @NotNull
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "provider_transaction_id", length = 64, unique = true)
    private String providerTransactionId;

    @DecimalMin(value = "0.00",inclusive = false)
    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "failure_reason",length = 255)
    private String failureReason;

    @Column(name = "paid_at")
    private Instant paidAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public static Payment create(UUID orderId, String currency, BigDecimal amount){
        Payment p = new Payment();
        p.orderId = orderId;
        p.currency = currency;
        p.amount = amount;
        p.paymentStatus = PaymentStatus.PENDING;
        return p;
    }

    public void markFailed(String reason){
        requireStatus(PaymentStatus.PENDING);
        this.failureReason = reason;
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public void markRefunded(){
        requireStatus(PaymentStatus.PAID);
        this.paymentStatus = PaymentStatus.REFUNDED;
    }

    public void markPaid(String providerTransactionId){
        requireStatus(PaymentStatus.PENDING);
        this.paymentStatus = PaymentStatus.PAID;
        this.providerTransactionId = providerTransactionId;
        this.paidAt = Instant.now();
    }

    private void requireStatus(PaymentStatus expected) {
        if (this.paymentStatus != expected) {
            throw new IllegalStateException("Beklenen: " + expected + ", mevcut: " + paymentStatus);
        }
    }
}
