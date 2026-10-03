package com.orderplatform.orderservice.order.entity;

import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "order_items", indexes = {
        @Index(name = "ix_order_items_order_id", columnList = "order_id"),
        @Index(name = "ix_order_items_product_id", columnList = "product_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItems extends UuidV7Entity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "order_id",
            foreignKey = @ForeignKey(name = "fk_order_item_order")
    )
    private Order order;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @NotBlank
    @Column(name = "product_title", length = 500)
    private String productTitle;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @DecimalMin(value = "0.00", inclusive = false)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Positive
    @Column(name = "quantity")
    private int quantity;

    public static OrderItems create(UUID productId, String productTitle, String imageUrl,
                                    BigDecimal unitPrice, int quantity) {
        if (unitPrice == null || unitPrice.signum() <= 0) {
            throw new IllegalArgumentException("Birim fiyat 0 dan büyük olmalı");
        }
        if (productTitle == null || productTitle.isBlank()) {
            throw new IllegalArgumentException("Ürün başlığı boş olamaz");
        }
        OrderItems item = new OrderItems();
        item.productId = Objects.requireNonNull(productId, "productId boş olamaz");
        item.productTitle = productTitle;
        item.imageUrl = imageUrl;
        item.unitPrice = unitPrice;
        item.changeQuantity(quantity);
        return item;
    }

    public void changeQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Adet 0 dan büyük olmalı");
        }
        this.quantity = quantity;
    }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Sadece Order.addItem() çağırır.
    void assignTo(Order order) {
        this.order = order;
    }
}
