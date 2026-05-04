package com.eduflow.enrollment.service;

import com.eduflow.enrollment.dto.CourseDTO;
import com.eduflow.enrollment.dto.EnrollmentRequest;
import com.eduflow.enrollment.dto.EnrollmentResponse;
import com.eduflow.enrollment.entity.Enrollment;
import com.eduflow.enrollment.event.EnrollmentEvent;
import com.eduflow.enrollment.feign.CourseClient;
import com.eduflow.enrollment.repository.EnrollmentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseClient courseClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, 
                             CourseClient courseClient, 
                             KafkaTemplate<String, Object> kafkaTemplate) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseClient = courseClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @CircuitBreaker(name = "courseService", fallbackMethod = "fallbackEnroll")
    public EnrollmentResponse enroll(UUID userId, EnrollmentRequest request) {
        // Validate course existence via Feign
        CourseDTO course = courseClient.getCourse(request.getCourseId());
        
        // Check if already enrolled
        if (enrollmentRepository.findByUserIdAndCourseId(userId, request.getCourseId()).isPresent()) {
            throw new RuntimeException("User already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setCourseId(request.getCourseId());
        
        Enrollment saved = enrollmentRepository.save(enrollment);

        // Publish Kafka Event
        EnrollmentEvent event = new EnrollmentEvent(
            saved.getId(), 
            saved.getUserId(), 
            saved.getCourseId(), 
            "ENROLLMENT_CREATED"
        );
        kafkaTemplate.send("enrollment-events", event);

        return mapToResponse(saved);
    }

    public EnrollmentResponse fallbackEnroll(UUID userId, EnrollmentRequest request, Throwable t) {
        throw new RuntimeException("Course Service is currently unavailable. Please try again later. " + t.getMessage());
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
        EnrollmentResponse response = new EnrollmentResponse();
        response.setId(enrollment.getId());
        response.setUserId(enrollment.getUserId());
        response.setCourseId(enrollment.getCourseId());
        response.setEnrolledAt(enrollment.getEnrolledAt());
        response.setStatus(enrollment.getStatus().name());
        response.setProgressPercent(enrollment.getProgressPercent());
        return response;
    }

    public EnrollmentRepository getEnrollmentRepository() {
        return enrollmentRepository;
    }

    public CourseClient getCourseClient() {
        return courseClient;
    }

    public KafkaTemplate<String, Object> getKafkaTemplate() {
        return kafkaTemplate;
    }
}
