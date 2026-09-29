package com.orderplatform.userservice.outbox.entity;

import com.orderplatform.common.enums.OutboxStatus;
import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "outbox_events", indexes = {
        @Index(name = "ix_outbox_status_created", columnList = "status, created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent extends UuidV7Entity {

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String topic;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status = OutboxStatus.NEW;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    private Instant publishedAt;

    @Column(nullable = false)
    private int attemptCount = 0;

    public static OutboxEvent create(UUID aggregateId, String topic, String payload) {
        OutboxEvent event = new OutboxEvent();
        event.aggregateId = aggregateId;
        event.topic = topic;
        event.payload = payload;
        return event;
    }

    public void markSent(Instant now) {
        this.status = OutboxStatus.SENT;
        this.publishedAt = now;
    }

    public void markAttemptFailed(int maxAttempts) {
        this.attemptCount++;
        if (this.attemptCount >= maxAttempts) {
            this.status = OutboxStatus.FAILED;
        }
    }
}
