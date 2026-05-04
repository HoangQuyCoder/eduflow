package com.eduflow.identity.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

   private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String userId = request.getHeader("X-User-Id");
        final String rolesHeader = request.getHeader("X-User-Roles");

        if (userId != null && rolesHeader != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Authenticating using headers from Gateway for user: {}", userId);
            
            List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesHeader.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    authorities
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

    public JwtService getJwtService() {
        return jwtService;
    }

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public static JwtAuthenticationFilterBuilder builder() {
        return new JwtAuthenticationFilterBuilder();
    }
    
    public static class JwtAuthenticationFilterBuilder {
        private JwtService jwtService;
        
        public JwtAuthenticationFilterBuilder jwtService(JwtService jwtService) {
            this.jwtService = jwtService;
            return this;
        }

        public JwtAuthenticationFilter build() {
            return new JwtAuthenticationFilter(jwtService);
        }
    }

    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }
}
