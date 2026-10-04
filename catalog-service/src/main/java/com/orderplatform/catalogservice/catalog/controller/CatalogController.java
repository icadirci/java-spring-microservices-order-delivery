package com.orderplatform.catalogservice.catalog.controller;

import com.orderplatform.catalogservice.catalog.dto.BrandResponse;
import com.orderplatform.catalogservice.catalog.dto.CatalogCategoryResponse;
import com.orderplatform.catalogservice.catalog.dto.CatalogProductResponse;
import com.orderplatform.catalogservice.catalog.dto.ProductSearchRequest;
import com.orderplatform.catalogservice.catalog.dto.ReviewResponse;
import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.common.dto.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// TODO: Implement. Gateway için /api/catalog/** route'u eklenmeli.
@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<List<CatalogCategoryResponse>> categories() {
        return null;
    }

    @GetMapping("/brands")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<List<BrandResponse>> brands(@RequestParam(required = false) String category) {
        return null;
    }

    @GetMapping("/products")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<PageResponse<CatalogProductResponse>> products(ProductSearchRequest request) {
        return null;
    }

    @GetMapping("/products/featured")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<List<CatalogProductResponse>> featured() {
        return null;
    }

    @GetMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<CatalogProductResponse> getProduct(@PathVariable UUID id) {
        return null;
    }

    @GetMapping("/products/{id}/similar")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<List<CatalogProductResponse>> similar(@PathVariable UUID id) {
        return null;
    }

    @GetMapping("/products/{id}/reviews")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<List<ReviewResponse>> reviews(@PathVariable UUID id) {
        return null;
    }
}
