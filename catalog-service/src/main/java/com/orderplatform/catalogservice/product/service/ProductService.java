package com.orderplatform.catalogservice.product.service;

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

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
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
        Product product = new Product(
                request.title(),
                request.image(),
                request.price(),
                request.status(),
                userId
        );

        Product savedProduct = productRepository.save(product);

        return ProductResponse.from(savedProduct);
    }

}
