package com.orderplatform.orderservice.order.entity;

import com.orderplatform.common.enums.PaymentStatus;
import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "ux_orders_order_number", columnList = "order_number", unique = true),
        @Index(name = "ix_orders_user_id", columnList = "user_id"),
        @Index(name = "ix_orders_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends UuidV7Entity {

    @NotBlank
    @Column(name = "order_number", length = 64, nullable = false)
    private String orderNumber;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @DecimalMin(value = "0.00", inclusive = false)
    @Column(name = "sub_total", precision = 19, scale = 2)
    private BigDecimal subTotal;

    @DecimalMin("0.00")
    @Column(name = "shipping_fee", precision = 19, scale = 2)
    private BigDecimal shippingFee;

    @DecimalMin(value = "0.00", inclusive = false)
    @Column(name = "total", precision = 19, scale = 2)
    private BigDecimal total;

    @NotBlank
    @Column(name = "shipping_address", length = 500)
    private String shippingAddress;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItems> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();

    /**
     * Kalem eklenmeden oluşturulursa toplamlar 0 kalır; addItem() ile doldurulmalı.
     */
    public Order(UUID userId, String address) {
        this.userId = Objects.requireNonNull(userId, "userId boş olamaz");
        this.shippingAddress = requireAddress(address);
        this.orderNumber = generateOrderNumber();
        this.status = OrderStatus.CREATED;
        this.paymentStatus = PaymentStatus.PENDING;
        this.subTotal = BigDecimal.ZERO;
        this.shippingFee = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.statusHistory.add(OrderStatusHistory.record(this, OrderStatus.CREATED, "Sipariş oluşturuldu"));
    }

    public static Order create(UUID userId, String address, List<OrderItems> items, BigDecimal shippingFee) {
        Order order = new Order(userId, address);
        items.forEach(order::addItem);
        order.applyShippingFee(shippingFee);
        return order;
    }

    // ---- kalemler & tutarlar ----

    public void addItem(OrderItems item) {
        requireStatus(OrderStatus.CREATED);
        Objects.requireNonNull(item, "item boş olamaz");
        items.add(item);
        item.assignTo(this);
        recalculateTotals();
    }

    public void removeItem(OrderItems item) {
        requireStatus(OrderStatus.CREATED);
        if (items.remove(item)) {
            recalculateTotals();
        }
    }

    public void applyShippingFee(BigDecimal fee) {
        requireStatus(OrderStatus.CREATED);
        if (fee == null || fee.signum() < 0) {
            throw new IllegalArgumentException("Kargo ücreti negatif olamaz");
        }
        this.shippingFee = fee;
        recalculateTotals();
    }

    public void changeShippingAddress(String address) {
        requireStatus(OrderStatus.CREATED);
        this.shippingAddress = requireAddress(address);
    }

    private void recalculateTotals() {
        this.subTotal = items.stream()
                .map(OrderItems::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.total = subTotal.add(shippingFee);
    }

    // ---- durum geçişleri ----

    /**
     * Servis katmanındaki genel durum güncellemesi için; geçişi ilgili domain metoduna yönlendirir.
     */
    public void changeStatus(OrderStatus target) {
        switch (target) {
            case PAID -> markPaid();
            case SHIPPED -> markShipped();
            case DELIVERED -> markDelivered();
            case CANCELLED -> cancel(null);
            default -> throw new IllegalStateException("Siparişi " + target + " durumuna geri alamazsın");
        }
    }

    public void markPaid() {
        requireStatus(OrderStatus.CREATED);
        this.paymentStatus = PaymentStatus.PAID;
        transitionTo(OrderStatus.PAID, "Ödeme alındı");
    }

    public void markPaymentFailed(String reason) {
        requireStatus(OrderStatus.CREATED);
        this.paymentStatus = PaymentStatus.FAILED;
        recordHistory("Ödeme başarısız: " + reason);
    }

    public void markShipped() {
        requireStatus(OrderStatus.PAID);
        transitionTo(OrderStatus.SHIPPED, "Kargoya verildi");
    }

    public void markDelivered() {
        requireStatus(OrderStatus.SHIPPED);
        transitionTo(OrderStatus.DELIVERED, "Teslim edildi");
    }

    public void cancel(String reason) {
        if (status != OrderStatus.CREATED && status != OrderStatus.PAID) {
            throw new IllegalStateException("Sipariş " + status + " durumunda iptal edilemez");
        }
        transitionTo(OrderStatus.CANCELLED, reason == null || reason.isBlank() ? "Sipariş iptal edildi" : reason);
    }

    /**
     * İptal edilen ve ödemesi alınmış siparişin ödeme iadesi tamamlandığında çağrılır.
     */
    public void markRefunded() {
        if (status != OrderStatus.CANCELLED || paymentStatus != PaymentStatus.PAID) {
            throw new IllegalStateException("İade için sipariş iptal edilmiş ve ödenmiş olmalı");
        }
        this.paymentStatus = PaymentStatus.REFUNDED;
        recordHistory("Ödeme iade edildi");
    }

    public boolean isOwnedBy(UUID userId) {
        return this.userId.equals(userId);
    }

    public boolean isCancellable() {
        return status == OrderStatus.CREATED || status == OrderStatus.PAID;
    }

    private void transitionTo(OrderStatus target, String info) {
        this.status = target;
        recordHistory(info);
    }

    private void recordHistory(String info) {
        statusHistory.add(OrderStatusHistory.record(this, status, info));
    }

    private void requireStatus(OrderStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException("Beklenen: " + expected + ", mevcut: " + status);
        }
    }

    private static String requireAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Teslimat adresi boş olamaz");
        }
        return address.trim();
    }

    private static String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
