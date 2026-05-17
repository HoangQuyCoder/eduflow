package com.eduflow.enrollment.service;

import com.eduflow.enrollment.dto.CourseDTO;
import com.eduflow.enrollment.dto.EnrollmentRequest;
import com.eduflow.enrollment.dto.EnrollmentResponse;
import com.eduflow.enrollment.entity.Enrollment;
import com.eduflow.enrollment.event.EnrollmentEvent;
import com.eduflow.enrollment.feign.CourseClient;
import com.eduflow.enrollment.repository.EnrollmentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseClient courseClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    @CircuitBreaker(name = "courseService", fallbackMethod = "fallbackEnroll")
    public EnrollmentResponse enroll(UUID userId, EnrollmentRequest request) {
        log.info("Enrolling user: {} in course: {}", userId, request.getCourseId());
        // Validate course existence via Feign
        CourseDTO course = courseClient.getCourse(request.getCourseId());
        if (course == null) {
            throw new RuntimeException("Course not found");
        }

        // Check if already enrolled
        if (enrollmentRepository.findByUserIdAndCourseId(userId, request.getCourseId()).isPresent()) {
            throw new RuntimeException("User already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .userId(userId)
                .courseId(request.getCourseId())
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);

        // Publish Kafka Event
        EnrollmentEvent event = new EnrollmentEvent(
                saved.getId(),
                saved.getUserId(),
                saved.getCourseId(),
                "ENROLLMENT_CREATED");
        kafkaTemplate.send("enrollment-events", event);

        return mapToResponse(saved);
    }

    public EnrollmentResponse fallbackEnroll(UUID userId, EnrollmentRequest request, Throwable t) {
        throw new RuntimeException(
                "Course Service is currently unavailable. Please try again later. " + t.getMessage());
    }

    public List<EnrollmentResponse> getUserEnrollments(UUID userId) {
        return enrollmentRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EnrollmentResponse getEnrollmentById(UUID enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .map(this::mapToResponse)
                .orElse(null);
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .userId(enrollment.getUserId())
                .courseId(enrollment.getCourseId())
                .enrolledAt(enrollment.getEnrolledAt())
                .status(enrollment.getStatus().name())
                .progressPercent(enrollment.getProgressPercent())
                .build();
    }
}

