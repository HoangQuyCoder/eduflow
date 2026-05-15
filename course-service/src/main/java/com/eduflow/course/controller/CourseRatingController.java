package com.eduflow.course.controller;

import com.eduflow.course.dto.CourseRatingDTO;
import com.eduflow.course.service.CourseRatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings")
@Validated
@RequiredArgsConstructor
@Slf4j
public class CourseRatingController {

    private final CourseRatingService courseRatingService;

    /**
     * Rate or update rating for a course
     */
    @PostMapping("/courses/{courseId}")
    public ResponseEntity<CourseRatingDTO> rateOrUpdateCourse(
            @PathVariable String courseId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CourseRatingDTO ratingDTO) {
        log.info("Creating/updating rating for course: {} by user: {}", courseId, userId);
        
        CourseRatingDTO savedRating = courseRatingService.rateOrUpdateCourse(courseId, userId, ratingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRating);
    }

    /**
     * Delete a rating
     */
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(@PathVariable String ratingId) {
        log.info("Deleting rating with ID: {}", ratingId);
        
        courseRatingService.deleteRating(ratingId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get rating by ID
     */
    @GetMapping("/{ratingId}")
    public ResponseEntity<CourseRatingDTO> getRatingById(@PathVariable String ratingId) {
        log.info("Fetching rating with ID: {}", ratingId);
        
        CourseRatingDTO rating = courseRatingService.getRatingById(ratingId);
        return ResponseEntity.ok(rating);
    }

    /**
     * Get all ratings for a course
     */
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<List<CourseRatingDTO>> getRatingsByCourseId(@PathVariable String courseId) {
        log.info("Fetching ratings for course: {}", courseId);
        
        List<CourseRatingDTO> ratings = courseRatingService.getRatingsByCourseId(courseId);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Get ratings for a course with pagination
     */
    @GetMapping("/courses/{courseId}/paged")
    public ResponseEntity<Page<CourseRatingDTO>> getRatingsByCourseIdPaged(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Fetching ratings (paged) for course: {}", courseId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseRatingDTO> ratings = courseRatingService.getRatingsByCourseIdPaged(courseId, pageable);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Get user's rating for a course
     */
    @GetMapping("/courses/{courseId}/user")
    public ResponseEntity<CourseRatingDTO> getUserRatingForCourse(
            @PathVariable String courseId,
            @RequestHeader("X-User-Id") String userId) {
        log.info("Fetching rating for course: {} by user: {}", courseId, userId);
        
        CourseRatingDTO rating = courseRatingService.getUserRatingForCourse(courseId, userId);
        return ResponseEntity.ok(rating);
    }

    /**
     * Get total rating count for a course
     */
    @GetMapping("/courses/{courseId}/count")
    public ResponseEntity<Long> getRatingCount(@PathVariable String courseId) {
        log.info("Counting ratings for course: {}", courseId);
        
        Long count = courseRatingService.getRatingCountByCourseId(courseId);
        return ResponseEntity.ok(count);
    }

}

