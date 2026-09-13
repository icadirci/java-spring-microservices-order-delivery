package com.orderplatform.infra.persistence;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

import java.util.UUID;

@MappedSuperclass
public abstract class UuidV7Entity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @PrePersist
    private void generateId() {
        if (id == null) {
            id = UuidCreator.getTimeOrderedEpoch();
        }
    }

    public UUID getId() {
        return id;
    }
}
