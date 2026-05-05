package com.eduflow.identity.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_roles", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role"}))
public class UserRole {
    
    @Id
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID();
        createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserRole() {}

    public UserRole(UUID id, User user, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static UserRoleBuilder builder() {
        return new UserRoleBuilder();
    }
    
    public static class UserRoleBuilder {
        private UUID id; private User user; private Role role; private LocalDateTime createdAt;
        
        public UserRoleBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserRoleBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserRoleBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserRoleBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserRole build() {
            return new UserRole(id, user, role, createdAt);
        }
    }
}
