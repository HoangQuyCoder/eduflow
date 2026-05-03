package com.eduflow.course.repository;

import com.eduflow.course.entity.CourseRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRatingRepository extends MongoRepository<CourseRating, String> {
    List<CourseRating> findByCourseId(String courseId);

    Page<CourseRating> findByCourseId(String courseId, Pageable pageable);

    Optional<CourseRating> findByIdAndCourseId(String id, String courseId);

    Optional<CourseRating> findByCourseIdAndUserId(String courseId, String userId);

    Long countByCourseId(String courseId);
}
