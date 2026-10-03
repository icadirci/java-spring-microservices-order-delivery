package com.orderplatform.catalogservice.category.entity;

import com.orderplatform.catalogservice.common.SlugUtils;
import com.orderplatform.catalogservice.product.entity.Product;
import com.orderplatform.common.enums.GeneralStatus;
import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "ux_categories_slug", columnList = "slug", unique = true),
        @Index(name = "ix_categories_parent_category_id", columnList = "parent_category_id"),
        @Index(name = "ix_categories_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Category extends UuidV7Entity {

    protected Category() {
        // JPA
    }

    public Category(String title, GeneralStatus status, UUID authorId) {
        this.title = requireTitle(title);
        this.slug = SlugUtils.slugify(title);
        this.status = Objects.requireNonNull(status, "status boş olamaz");
        this.authorId = Objects.requireNonNull(authorId, "authorId boş olamaz");
    }

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String slug;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GeneralStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_category_id",
            foreignKey = @ForeignKey(name = "fk_categories_parent")
    )
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    @Column(nullable = false)
    private List<Category> subCategories = new ArrayList<>();

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    @Column(nullable = false)
    @NotNull
    private UUID authorId;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void rename(String title) {
        this.title = requireTitle(title);
        this.slug = SlugUtils.slugify(title);
    }

    public void changeSlug(String slug) {
        this.slug = SlugUtils.slugify(slug);
    }

    public void changeStatus(GeneralStatus status) {
        this.status = Objects.requireNonNull(status, "status boş olamaz");
    }

    public void activate() {
        requireNotDeleted();
        this.status = GeneralStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = GeneralStatus.INACTIVE;
    }

    public boolean isActive() {
        return status == GeneralStatus.ACTIVE && !isDeleted();
    }

    public void addSubCategory(Category child) {
        Objects.requireNonNull(child, "child boş olamaz").moveTo(this);
    }

    public void removeSubCategory(Category child) {
        if (subCategories.remove(child)) {
            child.parentCategory = null;
        }
    }

    /**
     * Kategoriyi yeni bir üst kategorinin altına taşır; null verilirse kök kategori olur.
     */
    public void moveTo(Category newParent) {
        for (Category c = newParent; c != null; c = c.parentCategory) {
            if (c == this) {
                throw new IllegalArgumentException("Kategori kendi alt ağacına taşınamaz");
            }
        }
        if (this.parentCategory != null) {
            this.parentCategory.subCategories.remove(this);
        }
        this.parentCategory = newParent;
        if (newParent != null) {
            newParent.subCategories.add(this);
        }
    }

    public boolean isRoot() {
        return parentCategory == null;
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    public void restore() {
        this.deletedAt = null;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    private void requireNotDeleted() {
        if (isDeleted()) {
            throw new IllegalStateException("Silinmiş kategori üzerinde işlem yapılamaz");
        }
    }

    private static String requireTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Kategori başlığı boş olamaz");
        }
        return title.trim();
    }
}
