package com.orderplatform.userservice.user.entity;

import com.orderplatform.common.security.Role;
import com.orderplatform.infra.persistence.UuidV7Entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends UuidV7Entity {

    @Column(nullable = false, length = 190)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 80)
    private String fullName;

    private String avatarUrl;

    @Column(length = 40)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean enabled = true;

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

    /**
     *
     * @param email
     * @param passwordHash
     * @param fullName
     * @return
     */
    public static User create(String email, String passwordHash, String fullName){
        User u = new User();
        u.email = email;
        u.passwordHash = passwordHash;
        u.fullName = fullName;
        return u;
    }

    /**
     *
     * @param address
     */
    public void addAddress(UserAddress address) {
        addresses.add(address);
        address.assignTo(this);
    }

    /**
     *
     * @param address
     */
    public void removeAddress(UserAddress address) {
        addresses.remove(address);
    }

    /**
     *
     * @param fullName
     * @param phone
     * @param avatarUrl
     */
    public void updateProfile(String fullName, String phone, String avatarUrl){
        this.fullName = fullName;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
    }

    /**
     *
     * @param newHash
     */
    public void changePassword(String newHash){
        this.passwordHash = newHash;
    }

    /**
     *
     */
    public void verifyEmail(){
        if (this.emailVerifiedAt == null){
            this.emailVerifiedAt = Instant.now();
        }
    }
}
