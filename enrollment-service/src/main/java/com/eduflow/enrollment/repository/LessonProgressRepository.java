package com.eduflow.enrollment.repository;

import com.eduflow.enrollment.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {
    List<LessonProgress> findByEnrollmentId(UUID enrollmentId);
    Optional<LessonProgress> findByEnrollmentIdAndLessonId(UUID enrollmentId, UUID lessonId);
}
