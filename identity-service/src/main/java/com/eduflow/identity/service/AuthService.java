package com.eduflow.identity.service;

import com.eduflow.identity.dto.AuthResponse;
import com.eduflow.identity.dto.LoginRequest;
import com.eduflow.identity.dto.RegisterRequest;
import com.eduflow.identity.entity.Role;
import com.eduflow.identity.entity.User;
import com.eduflow.identity.entity.UserProfile;
import com.eduflow.identity.entity.UserRole;
import com.eduflow.identity.repository.UserProfileRepository;
import com.eduflow.identity.repository.UserRepository;
import com.eduflow.identity.security.CustomUserDetails;
import com.eduflow.identity.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .build();

        UserRole defaultRole = UserRole.builder()
                .user(user)
                .role(Role.STUDENT)
                .build();

        user.getRoles().add(defaultRole);

        User savedUser = userRepository.save(user);

        UserProfile profile = UserProfile.builder()
                .user(savedUser)
                .build();
        userProfileRepository.save(profile);

        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .roles(savedUser.getRoles().stream()
                        .map(r -> r.getRole().name())
                        .collect(Collectors.toList()))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!Boolean.TRUE.equals(user.getAccountActive())) {
            throw new RuntimeException("User is not active");
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(r -> r.getRole().name())
                        .collect(Collectors.toList()))
                .build();
    }

    public AuthResponse refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid refresh token");
        }
        String refreshToken = authHeader.substring(7);
        String userId = jwtService.extractUserId(refreshToken);
        if (userId != null) {
            User user = userRepository.findById(java.util.UUID.fromString(userId))
                    .orElseThrow(() -> new RuntimeException("User not found"));
            CustomUserDetails userDetails = new CustomUserDetails(user);
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                String accessToken = jwtService.generateToken(userDetails);
                return AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .userId(user.getId())
                        .email(user.getEmail())
                        .roles(user.getRoles().stream()
                                .map(r -> r.getRole().name())
                                .collect(Collectors.toList()))
                        .build();
            }
        }
        throw new RuntimeException("Invalid refresh token");
    }

}

