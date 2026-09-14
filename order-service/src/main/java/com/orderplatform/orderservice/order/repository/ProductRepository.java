package com.orderplatform.orderservice.order.repository;

import com.orderplatform.orderservice.order.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByAuthorId(UUID authorId);
}
