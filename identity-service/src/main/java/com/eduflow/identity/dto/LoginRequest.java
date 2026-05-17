package com.eduflow.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for user login")
public class LoginRequest {
    @Schema(description = "User's email address", example = "user@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is not valid")
    private String email;

    @Schema(description = "User's password", example = "password123")
    @NotBlank(message = "Password is required")
    private String password;
}

