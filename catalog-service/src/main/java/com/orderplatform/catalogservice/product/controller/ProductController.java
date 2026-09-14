package com.orderplatform.catalogservice.product.controller;

import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.catalogservice.product.dto.CreateProductRequest;
import com.orderplatform.catalogservice.product.dto.ProductResponse;
import com.orderplatform.catalogservice.product.entity.Product;
import com.orderplatform.catalogservice.product.service.ProductService;
import jakarta.validation.Valid;
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
}
