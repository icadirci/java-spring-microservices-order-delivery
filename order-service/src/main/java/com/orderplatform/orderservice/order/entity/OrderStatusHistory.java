package com.orderplatform.orderservice.order.entity;

import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "order_status_history", indexes = {
        @Index(name = "ix_order_status_history_order_id", columnList = "order_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderStatusHistory extends UuidV7Entity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "info", length = 255)
    private String info;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    // Sadece Order içinden çağrılır; her durum değişikliği geçmişe yazılır.
    static OrderStatusHistory record(Order order, OrderStatus status, String info) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.order = order;
        history.status = status;
        history.info = info;
        return history;
    }
}
