package com.orderplatform.catalogservice.category.controller;

import com.orderplatform.catalogservice.category.dto.CategoryResponse;
import com.orderplatform.catalogservice.category.dto.CreateCategoryRequest;
import com.orderplatform.catalogservice.category.dto.CreateCategoryResponse;
import com.orderplatform.catalogservice.category.entity.Category;
import com.orderplatform.catalogservice.category.repository.CategoryRepository;
import com.orderplatform.catalogservice.category.service.CategoryService;
import com.orderplatform.catalogservice.product.dto.ProductResponse;
import com.orderplatform.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Endpoint contract only. Category persistence and business logic will be
 * added with the catalog domain implementation.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryService categoryService, CategoryRepository categoryRepository){
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping
    public ApiResponse<CreateCategoryResponse> create(@RequestBody @Valid CreateCategoryRequest request, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        CreateCategoryResponse response = categoryService.createCategory(request, userId);

        return ApiResponse.ok(response);
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAll() {
        return ApiResponse.ok(categoryService.getCategories());
    }

    @GetMapping("/{categoryId}")
    public ApiResponse<CategoryResponse> getById(@PathVariable UUID categoryId) {
        return ApiResponse.ok(categoryService.getById(categoryId));
    }

    @PutMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void update(@PathVariable UUID categoryId) {

    }

    @DeleteMapping("/{categoryId}")
    public void delete(@PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);
    }

    @GetMapping("/{categoryId}/products")
    public ApiResponse<List<ProductResponse>> getProducts(@PathVariable UUID categoryId) {
        return ApiResponse.ok(categoryService.getProducts(categoryId));
    }
}
