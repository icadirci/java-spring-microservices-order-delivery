package com.orderplatform.catalogservice.product.entity;

import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "product_favorites", indexes = {
        @Index(name = "ux_product_favorites_user_id_product_id", columnList = "user_id, product_id", unique = true),
        @Index(name = "ix_product_favorites_product_id", columnList = "product_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductFavorites extends UuidV7Entity {
    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Sadece Product.addFavorite() çağırır.
    static ProductFavorites create(UUID userId) {
        ProductFavorites favorite = new ProductFavorites();
        favorite.userId = Objects.requireNonNull(userId, "userId boş olamaz");
        return favorite;
    }

    void assignTo(Product product) {
        this.product = product;
    }
}
