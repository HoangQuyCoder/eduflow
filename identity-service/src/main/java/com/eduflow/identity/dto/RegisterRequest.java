package com.eduflow.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for user registration")
public class RegisterRequest {
    @Schema(description = "User's email address", example = "newuser@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is not valid")
    private String email;

    @Schema(description = "User's password (min 6 chars)", example = "securePassword123")
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Schema(description = "User's full name", example = "John Doe")
    @NotBlank(message = "Full name is required")
    private String fullName;
}

