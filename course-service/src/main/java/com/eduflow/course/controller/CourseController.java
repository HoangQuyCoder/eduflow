package com.eduflow.course.controller;

import com.eduflow.course.dto.CourseDTO;
import com.eduflow.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/courses")
@Validated
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    /**
     * Create a new course
     */
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(
            @Valid @RequestBody CourseDTO courseDTO,
            @RequestHeader("X-User-Id") String userId) {
        log.info("Creating course with title: {}", courseDTO.getTitle());
        
        CourseDTO createdCourse = courseService.createCourse(courseDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

    /**
     * Update a course
     */
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable String courseId,
            @Valid @RequestBody CourseDTO courseDTO,
            @RequestHeader("X-User-Id") String userId) {
        log.info("Updating course with ID: {}", courseId);
        
        CourseDTO updatedCourse = courseService.updateCourse(courseId, courseDTO, userId);
        return ResponseEntity.ok(updatedCourse);
    }

    /**
     * Publish or unpublish a course
     */
    @PatchMapping("/{courseId}/publish")
    public ResponseEntity<CourseDTO> publishCourse(
            @PathVariable String courseId,
            @RequestParam Boolean isPublished,
            @RequestHeader("X-User-Id") String userId) {
        log.info("Publishing course with ID: {} - isPublished: {}", courseId, isPublished);
        
        CourseDTO publishedCourse = courseService.publishCourse(courseId, isPublished, userId);
        return ResponseEntity.ok(publishedCourse);
    }

    /**
     * Delete a course
     */
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable String courseId,
            @RequestHeader("X-User-Id") String userId) {
        log.info("Deleting course with ID: {}", courseId);
        
        courseService.deleteCourse(courseId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get course by ID
     */
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable String courseId) {
        log.info("Fetching course with ID: {}", courseId);
        
        CourseDTO course = courseService.getCourseById(courseId);
        return ResponseEntity.ok(course);
    }

    /**
     * Get all published courses with pagination
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CourseDTO>> getAllCourses(
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        log.info("Fetching all courses - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<CourseDTO> courses = courseService.getAllCourses(pageable);
        return ResponseEntity.ok(courses);
    }

    /**
     * Get courses by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<CourseDTO>> getCoursesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Fetching courses by category: {}", category);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseDTO> courses = courseService.getCoursesByCategory(category, pageable);
        return ResponseEntity.ok(courses);
    }

    /**
     * Get courses by level
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<Page<CourseDTO>> getCoursesByLevel(
            @PathVariable String level,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Fetching courses by level: {}", level);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseDTO> courses = courseService.getCoursesByLevel(level, pageable);
        return ResponseEntity.ok(courses);
    }

    /**
     * Search courses by title
     */
    @GetMapping("/search")
    public ResponseEntity<Page<CourseDTO>> searchCourses(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Searching courses with title: {}", title);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseDTO> courses = courseService.searchCourses(title, pageable);
        return ResponseEntity.ok(courses);
    }

    /**
     * Get featured courses
     */
    @GetMapping("/featured")
    public ResponseEntity<List<CourseDTO>> getFeaturedCourses() {
        log.info("Fetching featured courses");
        
        List<CourseDTO> courses = courseService.getFeaturedCourses();
        return ResponseEntity.ok(courses);
    }

    /**
     * Get courses by instructor
     */
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<Page<CourseDTO>> getCoursesByInstructor(
            @PathVariable String instructorId,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Fetching courses by instructor: {}", instructorId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseDTO> courses = courseService.getCoursesByInstructor(instructorId, pageable);
        return ResponseEntity.ok(courses);
    }

    /**
     * Check if course exists (internal endpoint)
     */
    @GetMapping("/{courseId}/exists")
    public ResponseEntity<Boolean> courseExists(@PathVariable String courseId) {
        log.info("Checking if course exists: {}", courseId);
        
        Boolean exists = courseService.courseExists(courseId);
        return ResponseEntity.ok(exists);
    }

    /**
     * Update enrollment count (internal endpoint)
     */
    @PatchMapping("/{courseId}/enrollment-count")
    public ResponseEntity<Void> updateEnrollmentCount(
            @PathVariable String courseId,
            @RequestParam Integer increment) {
        log.info("Updating enrollment count for course: {} with increment: {}", courseId, increment);
        
        courseService.updateEnrollmentCount(courseId, increment);
        return ResponseEntity.noContent().build();
    }

}

