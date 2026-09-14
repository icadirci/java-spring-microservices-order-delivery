package com.orderplatform.orderservice.order.service;

import com.orderplatform.orderservice.order.dto.product.CreateProductRequest;
import com.orderplatform.orderservice.order.dto.product.ProductResponse;
import com.orderplatform.orderservice.order.entity.Product;
import com.orderplatform.orderservice.order.repository.ProductRepository;
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
