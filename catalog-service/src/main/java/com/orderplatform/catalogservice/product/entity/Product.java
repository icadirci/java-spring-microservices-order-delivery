package com.orderplatform.catalogservice.product.entity;

import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Product extends UuidV7Entity {

    protected Product() {
    }

    public Product(String title, String image, BigDecimal price, ProductStatus status, UUID authorId) {
        this.title = title;
        this.image = image;
        this.price = price;
        this.status = status;
        this.authorId = authorId;
    }

    @Column(nullable = false)
    private UUID authorId;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String image;

    @DecimalMin(value = "0.00", inclusive = false)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @PositiveOrZero
    @Column(nullable = false)
    private int stock = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.DRAFT;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}
