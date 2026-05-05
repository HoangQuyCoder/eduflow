package com.eduflow.enrollment.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lesson_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"enrollment_id", "lesson_id"}))
public class LessonProgress {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Column(name = "lesson_id", nullable = false)
    private UUID lessonId;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "time_spent_minutes")
    private Integer timeSpentMinutes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isCompleted == null) isCompleted = false;
        if (timeSpentMinutes == null) timeSpentMinutes = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public UUID getLessonId() {
        return lessonId;
    }

    public void setLessonId(UUID lessonId) {
        this.lessonId = lessonId;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getTimeSpentMinutes() {
        return timeSpentMinutes;
    }

    public void setTimeSpentMinutes(Integer timeSpentMinutes) {
        this.timeSpentMinutes = timeSpentMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LessonProgress() {}

    public LessonProgress(UUID id, Enrollment enrollment, UUID lessonId, Boolean isCompleted, LocalDateTime completedAt, Integer timeSpentMinutes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.enrollment = enrollment;
        this.lessonId = lessonId;
        this.isCompleted = isCompleted;
        this.completedAt = completedAt;
        this.timeSpentMinutes = timeSpentMinutes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LessonProgressBuilder builder() {
        return new LessonProgressBuilder();
    }
    
    public static class LessonProgressBuilder {
        private UUID id; private Enrollment enrollment; private UUID lessonId; private Boolean isCompleted; private LocalDateTime completedAt; private Integer timeSpentMinutes; private LocalDateTime createdAt; private LocalDateTime updatedAt;
        
        public LessonProgressBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public LessonProgressBuilder enrollment(Enrollment enrollment) {
            this.enrollment = enrollment;
            return this;
        }

        public LessonProgressBuilder lessonId(UUID lessonId) {
            this.lessonId = lessonId;
            return this;
        }

        public LessonProgressBuilder isCompleted(Boolean isCompleted) {
            this.isCompleted = isCompleted;
            return this;
        }

        public LessonProgressBuilder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public LessonProgressBuilder timeSpentMinutes(Integer timeSpentMinutes) {
            this.timeSpentMinutes = timeSpentMinutes;
            return this;
        }

        public LessonProgressBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public LessonProgressBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public LessonProgress build() {
            return new LessonProgress(id, enrollment, lessonId, isCompleted, completedAt, timeSpentMinutes, createdAt, updatedAt);
        }
    }
}
