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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
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
                        request.getPassword()
                )
        );

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
        String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail)
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

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public UserProfileRepository getUserProfileRepository() {
        return userProfileRepository;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public JwtService getJwtService() {
        return jwtService;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public AuthService() {}

    public AuthService(UserRepository userRepository, UserProfileRepository userProfileRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public static AuthServiceBuilder builder() {
        return new AuthServiceBuilder();
    }
    
    public static class AuthServiceBuilder {
        private UserRepository userRepository; private UserProfileRepository userProfileRepository; private PasswordEncoder passwordEncoder; private JwtService jwtService; private AuthenticationManager authenticationManager;
        
        public AuthServiceBuilder userRepository(UserRepository userRepository) {
            this.userRepository = userRepository;
            return this;
        }

        public AuthServiceBuilder userProfileRepository(UserProfileRepository userProfileRepository) {
            this.userProfileRepository = userProfileRepository;
            return this;
        }

        public AuthServiceBuilder passwordEncoder(PasswordEncoder passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
            return this;
        }

        public AuthServiceBuilder jwtService(JwtService jwtService) {
            this.jwtService = jwtService;
            return this;
        }

        public AuthServiceBuilder authenticationManager(AuthenticationManager authenticationManager) {
            this.authenticationManager = authenticationManager;
            return this;
        }

        public AuthService build() {
            return new AuthService(userRepository, userProfileRepository, passwordEncoder, jwtService, authenticationManager);
        }
    }
}
