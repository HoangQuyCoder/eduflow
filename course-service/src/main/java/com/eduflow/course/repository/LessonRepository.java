package com.eduflow.course.repository;

import com.eduflow.course.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends MongoRepository<Lesson, String> {
    List<Lesson> findByCourseIdOrderByOrder(String courseId);

    Page<Lesson> findByCourseId(String courseId, Pageable pageable);

    Optional<Lesson> findByIdAndCourseId(String id, String courseId);

    List<Lesson> findByCourseIdAndIsPublishedTrue(String courseId);

    void deleteByCourseId(String courseId);

    Long countByCourseId(String courseId);
}
