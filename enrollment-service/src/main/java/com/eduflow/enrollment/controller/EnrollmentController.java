package com.eduflow.enrollment.controller;

import com.eduflow.enrollment.dto.EnrollmentRequest;
import com.eduflow.enrollment.dto.EnrollmentResponse;
import com.eduflow.enrollment.service.EnrollmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Enrollment Management", description = "Endpoints for managing student course enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "Enroll in a course", description = "Enrolls the authenticated user into a specific course.")
    @PostMapping
    public ResponseEntity<EnrollmentResponse> enroll(@RequestBody EnrollmentRequest request,
            @Parameter(description = "User ID passed by the API Gateway", hidden = true) @RequestHeader("X-User-Id") UUID userId) {
        EnrollmentResponse response = enrollmentService.enroll(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @Operation(summary = "Get my enrollments", description = "Retrieves all enrollments for the currently authenticated user.")
    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
            @Parameter(description = "User ID passed by the API Gateway", hidden = true) @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(enrollmentService.getUserEnrollments(userId));
    }

    @Operation(summary = "Get enrollment by ID", description = "Retrieves details of a specific enrollment.")
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollment(@PathVariable UUID id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

}

