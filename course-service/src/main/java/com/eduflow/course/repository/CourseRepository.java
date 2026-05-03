package com.eduflow.course.repository;

import com.eduflow.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    Page<Course> findByIsPublishedTrue(Pageable pageable);

    Page<Course> findByInstructorIdAndIsPublishedTrue(String instructorId, Pageable pageable);

    List<Course> findByIsPublishedTrueOrderByAverageRatingDescEnrollmentCountDesc();

    Page<Course> findByTitleContainingIgnoreCaseAndIsPublishedTrue(String title, Pageable pageable);

    Page<Course> findByCategoryAndIsPublishedTrue(String category, Pageable pageable);

    Page<Course> findByLevelAndIsPublishedTrue(String level, Pageable pageable);

    Optional<Course> findByIdAndIsPublishedTrue(String id);

    List<Course> findByInstructorId(String instructorId);

    @Query("{ 'id': { $in: ?0 } }")
    List<Course> findByIds(List<String> ids);
}
