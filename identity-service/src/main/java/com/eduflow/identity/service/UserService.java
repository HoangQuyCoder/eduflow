package com.eduflow.identity.service;

import com.eduflow.identity.dto.UserDTO;
import com.eduflow.identity.entity.User;
import com.eduflow.identity.entity.UserProfile;
import com.eduflow.identity.repository.UserProfileRepository;
import com.eduflow.identity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDTO.getFullName() != null) {
            user.setFullName(userDTO.getFullName());
        }

        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = UserProfile.builder().user(user).build();
        }

        if (userDTO.getAvatarUrl() != null) {
            profile.setAvatarUrl(userDTO.getAvatarUrl());
        }
        if (userDTO.getBio() != null) {
            profile.setBio(userDTO.getBio());
        }
        if (userDTO.getPhone() != null) {
            profile.setPhone(userDTO.getPhone());
        }

        userProfileRepository.save(profile);
        return mapToDTO(userRepository.save(user));
    }

    private UserDTO mapToDTO(User user) {
        UserProfile profile = user.getProfile();
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .isActive(user.getAccountActive())
                .roles(user.getRoles().stream().map(r -> r.getRole().name()).collect(Collectors.toList()))
                .avatarUrl(profile != null ? profile.getAvatarUrl() : null)
                .bio(profile != null ? profile.getBio() : null)
                .phone(profile != null ? profile.getPhone() : null)
                .createdAt(user.getCreatedAt())
                .build();
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public UserProfileRepository getUserProfileRepository() {
        return userProfileRepository;
    }

    public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public static UserServiceBuilder builder() {
        return new UserServiceBuilder();
    }

    public static class UserServiceBuilder {
        private UserRepository userRepository;
        private UserProfileRepository userProfileRepository;

        public UserServiceBuilder userRepository(UserRepository userRepository) {
            this.userRepository = userRepository;
            return this;
        }

        public UserServiceBuilder userProfileRepository(UserProfileRepository userProfileRepository) {
            this.userProfileRepository = userProfileRepository;
            return this;
        }

        public UserService build() {
            return new UserService(userRepository, userProfileRepository);
        }
    }
}
