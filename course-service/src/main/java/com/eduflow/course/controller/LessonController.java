package com.eduflow.course.controller;

import com.eduflow.course.dto.LessonDTO;
import com.eduflow.course.service.LessonService;
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
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
@Validated
@Slf4j
public class LessonController {

    private final LessonService lessonService;

    /**
     * Create a new lesson
     */
    @PostMapping
    public ResponseEntity<LessonDTO> createLesson(@Valid @RequestBody LessonDTO lessonDTO) {
        log.info("Creating lesson for course: {}", lessonDTO.getCourseId());
        
        LessonDTO createdLesson = lessonService.createLesson(lessonDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLesson);
    }

    /**
     * Update a lesson
     */
    @PutMapping("/{lessonId}")
    public ResponseEntity<LessonDTO> updateLesson(
            @PathVariable String lessonId,
            @Valid @RequestBody LessonDTO lessonDTO) {
        log.info("Updating lesson with ID: {}", lessonId);
        
        LessonDTO updatedLesson = lessonService.updateLesson(lessonId, lessonDTO);
        return ResponseEntity.ok(updatedLesson);
    }

    /**
     * Publish or unpublish a lesson
     */
    @PatchMapping("/{lessonId}/publish")
    public ResponseEntity<LessonDTO> publishLesson(
            @PathVariable String lessonId,
            @RequestParam Boolean isPublished) {
        log.info("Publishing lesson with ID: {} - isPublished: {}", lessonId, isPublished);
        
        LessonDTO publishedLesson = lessonService.publishLesson(lessonId, isPublished);
        return ResponseEntity.ok(publishedLesson);
    }

    /**
     * Delete a lesson
     */
    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> deleteLesson(@PathVariable String lessonId) {
        log.info("Deleting lesson with ID: {}", lessonId);
        
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get lesson by ID
     */
    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonDTO> getLessonById(@PathVariable String lessonId) {
        log.info("Fetching lesson with ID: {}", lessonId);
        
        LessonDTO lesson = lessonService.getLessonById(lessonId);
        return ResponseEntity.ok(lesson);
    }

    /**
     * Get all lessons for a course
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LessonDTO>> getLessonsByCourseId(@PathVariable String courseId) {
        log.info("Fetching lessons for course: {}", courseId);
        
        List<LessonDTO> lessons = lessonService.getLessonsByCourseId(courseId);
        return ResponseEntity.ok(lessons);
    }

    /**
     * Get published lessons for a course
     */
    @GetMapping("/course/{courseId}/published")
    public ResponseEntity<List<LessonDTO>> getPublishedLessonsByCourseId(@PathVariable String courseId) {
        log.info("Fetching published lessons for course: {}", courseId);
        
        List<LessonDTO> lessons = lessonService.getPublishedLessonsByCourseId(courseId);
        return ResponseEntity.ok(lessons);
    }

    /**
     * Get lessons for a course with pagination
     */
    @GetMapping("/course/{courseId}/paged")
    public ResponseEntity<Page<LessonDTO>> getLessonsByCourseIdPaged(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Fetching lessons (paged) for course: {}", courseId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LessonDTO> lessons = lessonService.getLessonsByCourseIdPaged(courseId, pageable);
        return ResponseEntity.ok(lessons);
    }

    /**
     * Get lesson count for a course
     */
    @GetMapping("/course/{courseId}/count")
    public ResponseEntity<Long> getLessonCount(@PathVariable String courseId) {
        log.info("Counting lessons for course: {}", courseId);
        
        Long count = lessonService.getLessonCountByCourseId(courseId);
        return ResponseEntity.ok(count);
    }
}
