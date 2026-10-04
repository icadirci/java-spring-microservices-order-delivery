package com.orderplatform.infra.persistence;

import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;

@MappedSuperclass
@SQLRestriction("deleted_at Is NULL")
public abstract class SoftDeletableEntity extends UuidV7Entity{
    private Instant deletedAt;

    public void softDelete() { this.deletedAt = Instant.now(); }
    public boolean isDeleted() { return deletedAt != null; }
}
