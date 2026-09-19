package com.orderplatform.catalogservice.category.entity;

import com.orderplatform.catalogservice.product.entity.Product;
import com.orderplatform.common.enums.GeneralStatus;
import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories")
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Category extends UuidV7Entity {

    public Category(){

    }

    public Category(String title, GeneralStatus status, UUID authorId){
        this.title = title;
        this.status = status;
        this.authorId = authorId;
    }

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GeneralStatus status;

    @Column(name = "deleted_at")
    private Instant deletedAt;

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
    @NotBlank @NotNull
    private UUID authorId;


    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;



    public void softDelete(){
        this.deletedAt = Instant.now();
    }

    public boolean isDeleted(){
        return deletedAt != null;
    }


}
