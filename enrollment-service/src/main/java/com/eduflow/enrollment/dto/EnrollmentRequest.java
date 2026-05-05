package com.eduflow.enrollment.dto;

import java.util.UUID;


public class EnrollmentRequest {
    private UUID courseId;

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public EnrollmentRequest() {}

    public EnrollmentRequest(UUID courseId) {
        this.courseId = courseId;
    }

    public static EnrollmentRequestBuilder builder() {
        return new EnrollmentRequestBuilder();
    }
    
    public static class EnrollmentRequestBuilder {
        private UUID courseId;
        
        public EnrollmentRequestBuilder courseId(UUID courseId) {
            this.courseId = courseId;
            return this;
        }

        public EnrollmentRequest build() {
            return new EnrollmentRequest(courseId);
        }
    }
}
