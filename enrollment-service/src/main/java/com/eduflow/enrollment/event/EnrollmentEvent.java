package com.eduflow.enrollment.event;

import java.util.UUID;

public class EnrollmentEvent {
    private UUID enrollmentId;
    private UUID userId;
    private UUID courseId;
    private String eventType;

    public EnrollmentEvent() {}
    public EnrollmentEvent(UUID enrollmentId, UUID userId, UUID courseId, String eventType) {
        this.enrollmentId = enrollmentId;
        this.userId = userId;
        this.courseId = courseId;
        this.eventType = eventType;
    }

    // Getters and Setters
    public UUID getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(UUID enrollmentId) { this.enrollmentId = enrollmentId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getCourseId() { return courseId; }
    public void setCourseId(UUID courseId) { this.courseId = courseId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
}
