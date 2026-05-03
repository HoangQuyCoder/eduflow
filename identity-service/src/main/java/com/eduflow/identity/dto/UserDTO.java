package com.eduflow.identity.dto;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
