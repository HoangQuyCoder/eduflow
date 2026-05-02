package com.eduflow.identity.security;

import com.eduflow.identity.entity.User;
import com.eduflow.identity.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public CustomUserDetailsService() {}

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static CustomUserDetailsServiceBuilder builder() {
        return new CustomUserDetailsServiceBuilder();
    }
    
    public static class CustomUserDetailsServiceBuilder {
        private UserRepository userRepository;
        
        public CustomUserDetailsServiceBuilder userRepository(UserRepository userRepository) {
            this.userRepository = userRepository;
            return this;
        }

        public CustomUserDetailsService build() {
            return new CustomUserDetailsService(userRepository);
        }
    }
}
