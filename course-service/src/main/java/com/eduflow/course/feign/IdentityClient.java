package com.eduflow.course.feign;

import com.eduflow.course.dto.external.UserDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service")
public interface IdentityClient {

    @GetMapping("/api/v1/users/{id}")
    @CircuitBreaker(name = "identityService", fallbackMethod = "getUserFallback")
    UserDTO getUser(@PathVariable("id") String id);

    default UserDTO getUserFallback(String id, Throwable throwable) {
        UserDTO fallback = new UserDTO();
        fallback.setId(null);
        fallback.setFullName("Unknown Instructor");
        return fallback;
    }
}
