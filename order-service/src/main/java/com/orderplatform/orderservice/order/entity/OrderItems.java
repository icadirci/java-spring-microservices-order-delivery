package com.orderplatform.orderservice.order.entity;

import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class OrderItems extends UuidV7Entity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "order_id",
            foreignKey = @ForeignKey(name = "fk_order_item_order")
    )
    private Order order;

    @Column(nullable = false)
    private UUID productId;

    @DecimalMin(value = "0.00", inclusive = false)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;


}
