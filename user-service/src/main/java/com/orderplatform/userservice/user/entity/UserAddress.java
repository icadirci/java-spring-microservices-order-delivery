package com.orderplatform.userservice.user.entity;

import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "user_addresses", indexes = {
        @Index(name = "ix_user_addresses_user_id", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserAddress extends UuidV7Entity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 100)
    private String recipientName;

    @Column(nullable = false, length = 40)
    private String phone;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 500)
    private String addressLine;

    @Column(length = 20, name = "postal_code")
    private String postalCode;

    @Column(name = "is_default", nullable = false)
    private boolean defaultAddress = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static UserAddress create(String title, String recipientName, String phone,
                                     String city, String district, String addressLine,
                                     String postalCode) {
        UserAddress address = new UserAddress();
        address.update(title, recipientName, phone, city, district, addressLine, postalCode);
        return address;
    }

    public void update(String title, String recipientName, String phone,
                       String city, String district, String addressLine,
                       String postalCode) {
        this.title = title;
        this.recipientName = recipientName;
        this.phone = phone;
        this.city = city;
        this.district = district;
        this.addressLine = addressLine;
        this.postalCode = postalCode;
    }

    public void markAsDefault() {
        this.defaultAddress = true;
    }

    public void unmarkDefault() {
        this.defaultAddress = false;
    }

    // Sadece User.addAddress() çağırır; ilişkinin iki tarafını birlikte bağlar.
    void assignTo(User user) {
        this.user = user;
    }
}
