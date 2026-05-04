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
    public ResponseEntity<EnrollmentResponse> enroll(@RequestBody EnrollmentRequest request, 
        @RequestHeader("X-User-Id") String userId) {
        EnrollmentResponse response = enrollmentService.enroll(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(enrollmentService.getUserEnrollments(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollment(@PathVariable UUID id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    public EnrollmentService getEnrollmentService() {
        return enrollmentService;
    }
}
