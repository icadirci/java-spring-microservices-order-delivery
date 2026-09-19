package com.orderplatform.catalogservice.category.service;

import com.orderplatform.catalogservice.category.dto.CategoryResponse;
import com.orderplatform.catalogservice.category.dto.CreateCategoryRequest;
import com.orderplatform.catalogservice.category.dto.CreateCategoryResponse;
import com.orderplatform.catalogservice.category.entity.Category;
import com.orderplatform.catalogservice.category.repository.CategoryRepository;
import com.orderplatform.catalogservice.product.dto.ProductResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public CreateCategoryResponse createCategory(CreateCategoryRequest request, UUID authorId){
        Category category = new Category(
                request.title(),
                request.status(),
                authorId
        );
        Category savedCategory = categoryRepository.save(category);

        return new CreateCategoryResponse(savedCategory.getId(), request.title(), request.status());
    }

    public List<CategoryResponse> getCategories(){
        List<Category> categories = categoryRepository.findAllByDeletedAtIsNull();
        return categories.stream().map(CategoryResponse::from).toList();
    }

    public CategoryResponse getById(UUID categoryId){
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() ->
                    new RuntimeException("Category not found")
                );
        return CategoryResponse.from(category);
    }

    public List<ProductResponse> getProducts(UUID categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found!"));
        return category.getProducts().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public void deleteCategory(UUID categoryId){
        Category category = categoryRepository
                .findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.softDelete();
    }
}
