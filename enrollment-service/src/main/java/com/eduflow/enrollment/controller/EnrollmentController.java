package com.eduflow.enrollment.controller;

import com.eduflow.enrollment.dto.EnrollmentRequest;
import com.eduflow.enrollment.dto.EnrollmentResponse;
import com.eduflow.enrollment.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enroll(@RequestBody EnrollmentRequest request) {
        // In a real app, extract userId from JWT/SecurityContext
        // For now, using a dummy UUID or we should expect it from header
        UUID userId = UUID.randomUUID(); 
        return ResponseEntity.ok(enrollmentService.enroll(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments() {
        UUID userId = UUID.randomUUID(); // Dummy
        return ResponseEntity.ok(enrollmentService.getUserEnrollments(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollment(@PathVariable UUID id) {
        // Implementation for details
        return ResponseEntity.notFound().build();
    }

    public EnrollmentService getEnrollmentService() {
        return enrollmentService;
    }
}
