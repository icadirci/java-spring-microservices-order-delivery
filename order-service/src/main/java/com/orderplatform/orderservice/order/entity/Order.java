package com.orderplatform.orderservice.order.entity;

import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "orders")
public class Order extends UuidV7Entity {
    // JWT’den gelen userId
    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Order() {
        // JPA
    }

    public Order(UUID userId, String address) {
        this.userId = userId;
        this.address = address;
        this.status = OrderStatus.CREATED;
    }


}
