package com.orderplatform.catalogservice.category.dto;

import com.orderplatform.common.enums.GeneralStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateCategoryRequest(
    @NotNull @NotBlank String title,
    @NotBlank GeneralStatus status,
    UUID parent_category_id
) {
}
