package com.orderplatform.catalogservice.product.controller;

import com.orderplatform.catalogservice.catalog.dto.CatalogProductResponse;
import com.orderplatform.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// TODO: Implement. Favoriler catalog-service içinde tutulduğu için burada; gateway'de
// /api/users/me/favorites/** route'u user-service route'undan ÖNCE tanımlanmalı.
@RestController
@RequestMapping("/api/users/me/favorites")
public class FavoriteController {

    @GetMapping
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<List<CatalogProductResponse>> getFavorites(Authentication authentication) {
        return null;
    }

    @PostMapping("/{productId}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<Void> addFavorite(@PathVariable UUID productId, Authentication authentication) {
        return null;
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<Void> removeFavorite(@PathVariable UUID productId, Authentication authentication) {
        return null;
    }
}
