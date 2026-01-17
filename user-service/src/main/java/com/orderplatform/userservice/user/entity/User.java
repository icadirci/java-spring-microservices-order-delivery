package com.orderplatform.userservice.user.entity;

import com.orderplatform.common.security.Role;
import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "ix_users_email", columnList = "email", unique = true)
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true, length = 190)
    private String email;

    @Setter
    @Column(nullable = false)
    private String passwordHash;

    @Setter
    @Column(nullable = false, length = 80)
    private String fullName;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @Setter
    @Column(nullable = false)
    private boolean enabled = true;

    // ---- getters/setters ----

    public Long getId() { return id; }

    public String getEmail() { return email; }

    public String getPasswordHash() { return passwordHash; }

    public String getFullName() { return fullName; }

    public Role getRole() { return role; }

    public boolean isEnabled() { return enabled; }
}
