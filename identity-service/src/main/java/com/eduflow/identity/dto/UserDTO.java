package com.eduflow.identity.dto;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String email;
    private String fullName;
    private Boolean isActive;
    private List<String> roles;
    private String avatarUrl;
    private String bio;
    private String phone;
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO() {}

    public UserDTO(UUID id, String email, String fullName, Boolean isActive, List<String> roles, String avatarUrl, String bio, String phone, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.isActive = isActive;
        this.roles = roles;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public static UserDTOBuilder builder() {
        return new UserDTOBuilder();
    }
    
    public static class UserDTOBuilder {
        private UUID id; private String email; private String fullName; private Boolean isActive; private List<String> roles; private String avatarUrl; private String bio; private String phone; private LocalDateTime createdAt;
        
        public UserDTOBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserDTOBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserDTOBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public UserDTOBuilder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public UserDTOBuilder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public UserDTOBuilder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public UserDTOBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(id, email, fullName, isActive, roles, avatarUrl, bio, phone, createdAt);
        }
    }
}
