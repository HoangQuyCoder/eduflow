package com.eduflow.identity.controller;

import com.eduflow.identity.dto.AuthResponse;
import com.eduflow.identity.dto.LoginRequest;
import com.eduflow.identity.dto.RegisterRequest;
import com.eduflow.identity.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(authService.refreshToken(authHeader));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken() {
        return ResponseEntity.ok().build();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public static AuthControllerBuilder builder() {
        return new AuthControllerBuilder();
    }

    public static class AuthControllerBuilder {
        private AuthService authService;

        public AuthControllerBuilder authService(AuthService authService) {
            this.authService = authService;
            return this;
        }

        public AuthController build() {
            return new AuthController(authService);
        }
    }
}
