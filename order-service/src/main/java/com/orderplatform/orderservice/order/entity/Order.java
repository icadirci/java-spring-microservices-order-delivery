package com.orderplatform.orderservice.order.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // JWT’den gelen userId
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Order() {
        // JPA
    }

    public Order(Long userId, String address) {
        this.userId = userId;
        this.address = address;
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAddress() {
        return address;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void markAsPaid() {
        this.status = OrderStatus.PAID;
    }

    public void markAsShipped() {
        this.status = OrderStatus.SHIPPED;
    }

}
