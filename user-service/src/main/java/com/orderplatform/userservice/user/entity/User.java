package com.orderplatform.userservice.user.entity;

import com.orderplatform.common.security.Role;
import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "users", indexes = {
        @Index(name = "ix_users_email", columnList = "email", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
public class User extends UuidV7Entity {

    @Setter
    @Column(nullable = false, length = 190)
    private String email;

    @Setter
    @Column(nullable = false)
    private String passwordHash;

    @Setter
    @Column(nullable = false, length = 80)
    private String fullName;

    @Setter
    @Column(length = 40)
    private String phone;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @Setter
    @Column(nullable = false)
    private boolean enabled = true;

    @Setter
    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAddress> addresses = new ArrayList<>();

    public void addAddress(UserAddress address) {
        addresses.add(address);
        address.assignTo(this);
    }

    public void removeAddress(UserAddress address) {
        addresses.remove(address);
    }
}
