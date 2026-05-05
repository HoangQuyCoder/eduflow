package com.eduflow.enrollment.dto;

import java.time.LocalDateTime;
import java.util.UUID;


public class EnrollmentResponse {
    private UUID id;
    private UUID userId;
    private UUID courseId;
    private LocalDateTime enrolledAt;
    private String status;
    private Integer progressPercent;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Integer progressPercent) {
        this.progressPercent = progressPercent;
    }

    public EnrollmentResponse() {}

    public EnrollmentResponse(UUID id, UUID userId, UUID courseId, LocalDateTime enrolledAt, String status, Integer progressPercent) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.enrolledAt = enrolledAt;
        this.status = status;
        this.progressPercent = progressPercent;
    }

    public static EnrollmentResponseBuilder builder() {
        return new EnrollmentResponseBuilder();
    }
    
    public static class EnrollmentResponseBuilder {
        private UUID id; private UUID userId; private UUID courseId; private LocalDateTime enrolledAt; private String status; private Integer progressPercent;
        
        public EnrollmentResponseBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public EnrollmentResponseBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public EnrollmentResponseBuilder courseId(UUID courseId) {
            this.courseId = courseId;
            return this;
        }

        public EnrollmentResponseBuilder enrolledAt(LocalDateTime enrolledAt) {
            this.enrolledAt = enrolledAt;
            return this;
        }

        public EnrollmentResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public EnrollmentResponseBuilder progressPercent(Integer progressPercent) {
            this.progressPercent = progressPercent;
            return this;
        }

        public EnrollmentResponse build() {
            return new EnrollmentResponse(id, userId, courseId, enrolledAt, status, progressPercent);
        }
    }
}
