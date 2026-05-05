package com.eduflow.course.event;

public class CourseEvent {
    private String type;
    private String courseId;
    private String instructorId;
    private long timestamp;

    // Constructor, Getters, Setters

    public CourseEvent(String type, String courseId, String instructorId, long timestamp) {
        this.type = type;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
