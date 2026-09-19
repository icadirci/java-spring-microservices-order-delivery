package com.orderplatform.catalogservice.category.repository;

import com.orderplatform.catalogservice.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAllByDeletedAtIsNull();

    Optional<Category> findByIdAndDeletedAtIsNull(UUID id);
}
