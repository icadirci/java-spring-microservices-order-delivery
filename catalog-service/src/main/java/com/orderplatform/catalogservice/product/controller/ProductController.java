package com.orderplatform.catalogservice.product.controller;

import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.catalogservice.product.dto.CreateProductRequest;
import com.orderplatform.catalogservice.product.dto.ProductResponse;
import com.orderplatform.catalogservice.product.entity.Product;
import com.orderplatform.catalogservice.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> allProducts(){
        return ApiResponse.ok(productService.allProducts());
    }

    @PostMapping
    public ApiResponse<ProductResponse> create(@RequestBody @Valid CreateProductRequest request, Authentication authentication){
        UUID userId = UUID.fromString(authentication.getName());

        ProductResponse productResponse = productService.create(request, userId);

        return ApiResponse.ok(productResponse);
    }

    // TODO: Implement product management and query endpoints.
    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void getById(@PathVariable UUID productId) {
    }

    @PutMapping("/{productId}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void update(@PathVariable UUID productId) {
    }

    @PatchMapping("/{productId}/status")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void updateStatus(@PathVariable UUID productId) {
    }

    @PatchMapping("/{productId}/price")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void updatePrice(@PathVariable UUID productId) {
    }

    @PatchMapping("/{productId}/stock")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void updateStock(@PathVariable UUID productId) {
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void delete(@PathVariable UUID productId) {
    }

}
