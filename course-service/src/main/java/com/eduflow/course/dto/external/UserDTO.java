package com.eduflow.course.dto.external;

import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String fullName;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public UserDTO() {}
}
