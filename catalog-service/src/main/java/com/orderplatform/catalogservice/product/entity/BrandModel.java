package com.orderplatform.catalogservice.product.entity;

import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "brand_models", indexes = {
        @Index(name = "ux_brand_models_brand_id_model_name", columnList = "brand_id, model_name", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandModel extends UuidV7Entity {
    @NotBlank
    @Column(name = "model_name", length = 100)
    private String modelName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    static BrandModel create(String modelName) {
        BrandModel model = new BrandModel();
        model.rename(modelName);
        return model;
    }

    public void rename(String modelName) {
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalArgumentException("Model adı boş olamaz");
        }
        this.modelName = modelName.trim();
    }

    // Sadece Brand.addModel() çağırır.
    void assignTo(Brand brand) {
        this.brand = brand;
    }
}
