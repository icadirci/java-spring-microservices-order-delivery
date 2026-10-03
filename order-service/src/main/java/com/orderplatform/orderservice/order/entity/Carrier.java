package com.orderplatform.orderservice.order.entity;

import com.orderplatform.common.enums.GeneralStatus;
import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carriers", indexes = {
        @Index(name = "ux_carriers_carrier_name", columnList = "carrier_name", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Carrier extends UuidV7Entity {
    @NotBlank
    @Column(name = "carrier_name", length = 100)
    private String carrierName;

    @Enumerated(EnumType.STRING)
    private GeneralStatus status;

    public static Carrier create(String carrierName) {
        Carrier carrier = new Carrier();
        carrier.rename(carrierName);
        carrier.status = GeneralStatus.ACTIVE;
        return carrier;
    }

    public void rename(String carrierName) {
        if (carrierName == null || carrierName.isBlank()) {
            throw new IllegalArgumentException("Kargo firması adı boş olamaz");
        }
        this.carrierName = carrierName.trim();
    }

    public void activate() {
        this.status = GeneralStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = GeneralStatus.INACTIVE;
    }

    public boolean isActive() {
        return status == GeneralStatus.ACTIVE;
    }
}
