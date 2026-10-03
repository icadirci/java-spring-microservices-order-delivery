package com.orderplatform.catalogservice.product.entity;

import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "product_reviews", indexes = {
        @Index(name = "ux_product_reviews_product_id_user_id", columnList = "product_id, user_id", unique = true),
        @Index(name = "ix_product_reviews_user_id", columnList = "user_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductReview extends UuidV7Entity {
    private static final BigDecimal MAX_RATING = BigDecimal.valueOf(5);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @DecimalMin(value = "0.00", inclusive = false)
    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(length = 255)
    private String comment;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    /**
     * Yorum oluşturulurken ürünün puan ortalaması da güncellenir.
     */
    public static ProductReview create(Product product, UUID userId, BigDecimal rating, String comment) {
        requireValidRating(rating);
        ProductReview review = new ProductReview();
        review.product = Objects.requireNonNull(product, "product boş olamaz");
        review.userId = Objects.requireNonNull(userId, "userId boş olamaz");
        review.rating = rating;
        review.comment = comment;
        product.addReviewRating(rating);
        return review;
    }

    public void edit(BigDecimal newRating, String comment) {
        requireValidRating(newRating);
        product.replaceReviewRating(this.rating, newRating);
        this.rating = newRating;
        this.comment = comment;
    }

    /**
     * Yorum repository'den silinmeden önce çağrılır; ürün puanını geri alır.
     */
    public void detach() {
        product.removeReviewRating(this.rating);
    }

    public boolean isOwnedBy(UUID userId) {
        return this.userId.equals(userId);
    }

    private static void requireValidRating(BigDecimal rating) {
        if (rating == null || rating.signum() <= 0 || rating.compareTo(MAX_RATING) > 0) {
            throw new IllegalArgumentException("Puan 0 ile 5 arasında olmalı");
        }
    }
}
