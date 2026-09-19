package com.orderplatform.catalogservice.category.dto;

import com.orderplatform.catalogservice.category.entity.Category;
import com.orderplatform.common.enums.GeneralStatus;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String title,
        GeneralStatus status
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
          category.getId(),
          category.getTitle(),
                category.getStatus()
        );
    }
}
