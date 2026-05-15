package com.eduflow.course.service;

import com.eduflow.course.dto.CourseRatingDTO;
import com.eduflow.course.entity.CourseRating;
import com.eduflow.course.exception.CourseNotFoundException;
import com.eduflow.course.exception.RatingNotFoundException;
import com.eduflow.course.repository.CourseRatingRepository;
import com.eduflow.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseRatingService {

    private final CourseRatingRepository courseRatingRepository;
    private final CourseRepository courseRepository;
    private final CourseService courseService;

    /**
     * Create or update a rating
     */
    @Transactional
    public CourseRatingDTO rateOrUpdateCourse(String courseId, String userId, CourseRatingDTO ratingDTO) {
        log.info("Creating/updating rating for course: {} by user: {}", courseId, userId);

        // Verify course exists
        courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        CourseRating rating = courseRatingRepository.findByCourseIdAndUserId(courseId, userId)
                .orElseGet(() -> new CourseRating());

        rating.setCourseId(courseId);
        rating.setUserId(userId);
        rating.setRating(ratingDTO.getRating());
        rating.setReview(ratingDTO.getReview());

        if (rating.getCreatedAt() == null) {
            rating.setCreatedAt(LocalDateTime.now());
        }

        CourseRating savedRating = courseRatingRepository.save(rating);
        log.info("Rating saved successfully for course: {}", courseId);

        // Update course average rating
        updateCourseAverageRating(courseId);

        return mapToDTO(savedRating);
    }

    /**
     * Delete a rating
     */
    @Transactional
    public void deleteRating(String ratingId) {
        log.info("Deleting rating with ID: {}", ratingId);

        CourseRating rating = courseRatingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found with ID: " + ratingId));

        courseRatingRepository.deleteById(ratingId);

        // Update course average rating
        updateCourseAverageRating(rating.getCourseId());

        log.info("Rating deleted successfully with ID: {}", ratingId);
    }

    /**
     * Get rating by ID
     */
    public CourseRatingDTO getRatingById(String ratingId) {
        log.info("Fetching rating with ID: {}", ratingId);

        CourseRating rating = courseRatingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found with ID: " + ratingId));

        return mapToDTO(rating);
    }

    /**
     * Get all ratings for a course
     */
    public List<CourseRatingDTO> getRatingsByCourseId(String courseId) {
        log.info("Fetching ratings for course: {}", courseId);

        // Verify course exists
        courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        return courseRatingRepository.findByCourseId(courseId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get ratings for a course with pagination
     */
    public Page<CourseRatingDTO> getRatingsByCourseIdPaged(String courseId, Pageable pageable) {
        log.info("Fetching ratings (paged) for course: {}", courseId);

        // Verify course exists
        courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        return courseRatingRepository.findByCourseId(courseId, pageable)
                .map(this::mapToDTO);
    }

    /**
     * Get user's rating for a course
     */
    public CourseRatingDTO getUserRatingForCourse(String courseId, String userId) {
        log.info("Fetching rating for course: {} by user: {}", courseId, userId);

        CourseRating rating = courseRatingRepository.findByCourseIdAndUserId(courseId, userId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found for course and user"));

        return mapToDTO(rating);
    }

    /**
     * Get total rating count for a course
     */
    public Long getRatingCountByCourseId(String courseId) {
        log.info("Counting ratings for course: {}", courseId);
        return courseRatingRepository.countByCourseId(courseId);
    }

    // Private helper methods

    @Transactional
    private void updateCourseAverageRating(String courseId) {
        log.info("Updating average rating for course: {}", courseId);

        List<CourseRating> ratings = courseRatingRepository.findByCourseId(courseId);

        if (ratings.isEmpty()) {
            courseService.updateAverageRating(courseId, 0.0, 0);
            return;
        }

        Double averageRating = ratings.stream()
                .mapToDouble(CourseRating::getRating)
                .average()
                .orElse(0.0);

        courseService.updateAverageRating(courseId, averageRating, ratings.size());
    }

    private CourseRatingDTO mapToDTO(CourseRating rating) {
        return CourseRatingDTO.builder()
                .id(rating.getId())
                .courseId(rating.getCourseId())
                .userId(rating.getUserId())
                .rating(rating.getRating())
                .review(rating.getReview())
                .createdAt(rating.getCreatedAt())
                .build();
    }

}

