package com.orderplatform.catalogservice.product.service;

import com.orderplatform.catalogservice.category.entity.Category;
import com.orderplatform.catalogservice.category.repository.CategoryRepository;
import com.orderplatform.catalogservice.product.dto.CreateProductRequest;
import com.orderplatform.catalogservice.product.dto.ProductResponse;
import com.orderplatform.catalogservice.product.entity.Product;
import com.orderplatform.catalogservice.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductResponse> allProducts(){
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse create(
            CreateProductRequest request,
            UUID userId
    ) {
        Category category = categoryRepository.findById(request.category())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Product product = new Product(
                request.title(),
                request.image(),
                request.price(),
                request.status(),
                userId,
                category
        );

        Product savedProduct = productRepository.save(product);

        return ProductResponse.from(savedProduct);
    }



}
