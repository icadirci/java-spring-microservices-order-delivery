package com.orderplatform.catalogservice.catalog.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * GET /products query parametreleri. subcategories ve brands virgulle ayrilmis (csv) gelir.
 * sort: popular | price_asc | price_desc | rating | newest
 */
public record ProductSearchRequest(
        String q,
        String category,
        List<String> subcategories,
        List<String> brands,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal minRating,
        String sort,
        Integer page,
        Integer size
) {
}
