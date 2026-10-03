package com.orderplatform.orderservice.order.entity;

import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "shipments", indexes = {
        @Index(name = "ux_shipments_order_id", columnList = "order_id", unique = true),
        @Index(name = "ux_shipments_carrier_id_tracking_number", columnList = "carrier_id, tracking_number", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shipment extends UuidV7Entity {
    @OneToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Bir kargo firmasının birçok gönderisi olabilir (OneToOne carrier başına tek gönderiye zorlardı).
    @ManyToOne(optional = false)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;

    @NotBlank
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "shipped_at")
    private Instant shippedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * Siparişi kargoya verir: sipariş SHIPPED olur ve gönderi kaydı oluşur.
     */
    public static Shipment dispatch(Order order, Carrier carrier, String trackingNumber) {
        Objects.requireNonNull(order, "order boş olamaz");
        Objects.requireNonNull(carrier, "carrier boş olamaz");
        if (!carrier.isActive()) {
            throw new IllegalStateException("Kargo firması aktif değil");
        }
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException("Takip numarası boş olamaz");
        }
        order.markShipped();
        Shipment shipment = new Shipment();
        shipment.order = order;
        shipment.carrier = carrier;
        shipment.trackingNumber = trackingNumber.trim();
        shipment.shippedAt = Instant.now();
        return shipment;
    }

    public void markDelivered() {
        if (deliveredAt != null) {
            throw new IllegalStateException("Gönderi zaten teslim edildi");
        }
        order.markDelivered();
        this.deliveredAt = Instant.now();
    }

    public boolean isDelivered() {
        return deliveredAt != null;
    }
}
