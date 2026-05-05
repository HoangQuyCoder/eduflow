package com.eduflow.enrollment.feign;

import com.eduflow.enrollment.dto.CourseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "course-service")
public interface CourseClient {
    @GetMapping("/api/v1/courses/{id}")
    CourseDTO getCourse(@PathVariable("id") UUID id);
}
