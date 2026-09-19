package com.orderplatform.catalogservice.product.dto;

import com.orderplatform.catalogservice.product.entity.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest (
        @NotBlank String title,
        String image,
        @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal price,
        @NotNull ProductStatus status,
        @NotNull UUID category
        ){
}
