package com.eduflow.enrollment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lesson_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"enrollment_id", "lesson_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
