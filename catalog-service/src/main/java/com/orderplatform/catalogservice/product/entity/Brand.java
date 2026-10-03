package com.orderplatform.catalogservice.product.entity;

import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brands", indexes = {
        @Index(name = "ux_brands_brand_name", columnList = "brand_name", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand extends UuidV7Entity {
    @NotBlank
    @Column(name = "brand_name")
    private String brandName;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BrandModel> brandModels = new ArrayList<>();

    public static Brand create(String brandName) {
        Brand brand = new Brand();
        brand.rename(brandName);
        return brand;
    }

    public void rename(String brandName) {
        if (brandName == null || brandName.isBlank()) {
            throw new IllegalArgumentException("Marka adı boş olamaz");
        }
        this.brandName = brandName.trim();
    }

    public BrandModel addModel(String modelName) {
        BrandModel model = BrandModel.create(modelName);
        if (hasModel(model.getModelName())) {
            throw new IllegalStateException("Bu marka altında model zaten var: " + model.getModelName());
        }
        brandModels.add(model);
        model.assignTo(this);
        return model;
    }

    public void removeModel(BrandModel model) {
        brandModels.remove(model);
    }

    public boolean hasModel(String modelName) {
        return brandModels.stream().anyMatch(m -> m.getModelName().equalsIgnoreCase(modelName));
    }
}
