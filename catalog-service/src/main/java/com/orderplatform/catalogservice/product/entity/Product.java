package com.orderplatform.catalogservice.product.entity;

import com.orderplatform.catalogservice.category.entity.Category;
import com.orderplatform.catalogservice.common.SlugUtils;
import com.orderplatform.catalogservice.product.dto.ProductColor;
import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "ux_products_slug", columnList = "slug", unique = true),
        @Index(name = "ix_products_category_id", columnList = "category_id"),
        @Index(name = "ix_products_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Product extends UuidV7Entity {

    protected Product() {
        // JPA
    }

    public Product(String title, String image, BigDecimal price, ProductStatus status, UUID authorId, Category category) {
        this.title = requireText(title, "Ürün başlığı");
        this.slug = SlugUtils.slugify(title);
        this.image = image;
        this.price = requirePositive(price);
        this.oldPrice = price;
        this.status = Objects.requireNonNull(status, "status boş olamaz");
        this.authorId = Objects.requireNonNull(authorId, "authorId boş olamaz");
        this.category = category;
    }

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(length = 500)
    private String slug;

    @Column(name = "sub_title", length = 255)
    private String subTitle;

    @NotBlank
    private String description;

    @OneToOne(mappedBy = "product")
    private BrandModel model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            foreignKey = @ForeignKey(name = "fk_product_categories")
    )
    private Category category;

    @DecimalMin(value = "0.00", inclusive = false)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @DecimalMin(value = "0.00", inclusive = false)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal oldPrice;

    @DecimalMin(value = "0.00", inclusive = false)
    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    private int reviewCount = 0;

    @Column(name = "image", length = 500)
    private String image;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> images;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<ProductColor> colors;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> specs;

    private Boolean isFeatured = false;

    @PositiveOrZero
    @Column(nullable = false)
    private int stock = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(nullable = false)
    private UUID authorId;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductFavorites> productFavorites = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ---- içerik ----

    public void updateDetails(String title, String subTitle, String description) {
        this.title = requireText(title, "Ürün başlığı");
        this.subTitle = subTitle;
        this.description = requireText(description, "Ürün açıklaması");
    }

    public void rename(String title) {
        this.title = requireText(title, "Ürün başlığı");
        this.slug = SlugUtils.slugify(title);
    }

    public void changeSlug(String slug) {
        this.slug = SlugUtils.slugify(slug);
    }

    public void assignCategory(Category category) {
        this.category = category;
    }

    public void updateImages(String mainImage, List<String> images) {
        this.image = mainImage;
        this.images = images == null ? null : new ArrayList<>(images);
    }

    public void updateColors(List<ProductColor> colors) {
        this.colors = colors == null ? null : new ArrayList<>(colors);
    }

    public void updateSpecs(Map<String, String> specs) {
        this.specs = specs;
    }

    // ---- fiyat ----

    /**
     * Fiyatı değiştirir; önceki fiyat oldPrice olarak saklanır (indirim gösterimi için).
     */
    public void changePrice(BigDecimal newPrice) {
        requirePositive(newPrice);
        if (newPrice.compareTo(this.price) != 0) {
            this.oldPrice = this.price;
            this.price = newPrice;
        }
    }

    public boolean isDiscounted() {
        return oldPrice != null && oldPrice.compareTo(price) > 0;
    }

    // ---- stok ----

    public boolean hasStock(int quantity) {
        return quantity > 0 && stock >= quantity;
    }

    public void increaseStock(int quantity) {
        requireQuantity(quantity);
        this.stock += quantity;
    }

    public void decreaseStock(int quantity) {
        requireQuantity(quantity);
        if (stock < quantity) {
            throw new IllegalStateException("Yetersiz stok. Mevcut: " + stock + ", istenen: " + quantity);
        }
        this.stock -= quantity;
    }

    // ---- durum ----

    public void publish() {
        if (stock <= 0) {
            throw new IllegalStateException("Stoğu olmayan ürün yayınlanamaz");
        }
        this.status = ProductStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
    }

    public boolean isActive() {
        return status == ProductStatus.ACTIVE;
    }

    public void feature() {
        this.isFeatured = true;
    }

    public void unfeature() {
        this.isFeatured = false;
    }

    // ---- puan ----

    public void addReviewRating(BigDecimal reviewRating) {
        BigDecimal total = currentRating().multiply(BigDecimal.valueOf(reviewCount)).add(reviewRating);
        this.reviewCount += 1;
        this.rating = total.divide(BigDecimal.valueOf(reviewCount), 2, RoundingMode.HALF_UP);
    }

    public void replaceReviewRating(BigDecimal oldReviewRating, BigDecimal newReviewRating) {
        if (reviewCount == 0) {
            throw new IllegalStateException("Güncellenecek yorum puanı yok");
        }
        BigDecimal total = currentRating().multiply(BigDecimal.valueOf(reviewCount))
                .subtract(oldReviewRating).add(newReviewRating);
        this.rating = total.divide(BigDecimal.valueOf(reviewCount), 2, RoundingMode.HALF_UP);
    }

    public void removeReviewRating(BigDecimal reviewRating) {
        if (reviewCount == 0) {
            throw new IllegalStateException("Silinecek yorum puanı yok");
        }
        BigDecimal total = currentRating().multiply(BigDecimal.valueOf(reviewCount)).subtract(reviewRating);
        this.reviewCount -= 1;
        this.rating = reviewCount == 0
                ? null
                : total.divide(BigDecimal.valueOf(reviewCount), 2, RoundingMode.HALF_UP);
    }

    // ---- favoriler ----

    public void addFavorite(UUID userId) {
        if (isFavoritedBy(userId)) {
            return;
        }
        ProductFavorites favorite = ProductFavorites.create(userId);
        productFavorites.add(favorite);
        favorite.assignTo(this);
    }

    public void removeFavorite(UUID userId) {
        productFavorites.removeIf(f -> f.getUserId().equals(userId));
    }

    public boolean isFavoritedBy(UUID userId) {
        return productFavorites.stream().anyMatch(f -> f.getUserId().equals(userId));
    }

    private BigDecimal currentRating() {
        return rating == null ? BigDecimal.ZERO : rating;
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " boş olamaz");
        }
        return value.trim();
    }

    private static BigDecimal requirePositive(BigDecimal price) {
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("Fiyat 0'dan büyük olmalı");
        }
        return price;
    }

    private static void requireQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Miktar 0'dan büyük olmalı");
        }
    }
}
