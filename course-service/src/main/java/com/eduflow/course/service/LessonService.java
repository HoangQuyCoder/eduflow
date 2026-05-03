package com.eduflow.course.service;

import com.eduflow.course.dto.LessonDTO;
import com.eduflow.course.entity.Lesson;
import com.eduflow.course.exception.CourseNotFoundException;
import com.eduflow.course.exception.LessonNotFoundException;
import com.eduflow.course.repository.CourseRepository;
import com.eduflow.course.repository.LessonRepository;
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
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    /**
     * Create a new lesson
     */
    @Transactional
    public LessonDTO createLesson(LessonDTO lessonDTO) {
        log.info("Creating lesson for course: {}", lessonDTO.getCourseId());

        // Verify course exists
        courseRepository.findById(lessonDTO.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + lessonDTO.getCourseId()));

        Lesson lesson = Lesson.builder()
                .courseId(lessonDTO.getCourseId())
                .title(lessonDTO.getTitle())
                .content(lessonDTO.getContent())
                .videoUrl(lessonDTO.getVideoUrl())
                .order(lessonDTO.getOrder())
                .duration(lessonDTO.getDuration())
                .description(lessonDTO.getDescription())
                .isPublished(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("Lesson created successfully with ID: {}", savedLesson.getId());

        return mapToDTO(savedLesson);
    }

    /**
     * Update lesson
     */
    @Transactional
    public LessonDTO updateLesson(String lessonId, LessonDTO lessonDTO) {
        log.info("Updating lesson with ID: {}", lessonId);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with ID: " + lessonId));

        lesson.setTitle(lessonDTO.getTitle());
        lesson.setContent(lessonDTO.getContent());
        lesson.setVideoUrl(lessonDTO.getVideoUrl());
        lesson.setOrder(lessonDTO.getOrder());
        lesson.setDuration(lessonDTO.getDuration());
        lesson.setDescription(lessonDTO.getDescription());
        lesson.setUpdatedAt(LocalDateTime.now());

        Lesson updatedLesson = lessonRepository.save(lesson);
        log.info("Lesson updated successfully with ID: {}", lessonId);

        return mapToDTO(updatedLesson);
    }

    /**
     * Publish or unpublish lesson
     */
    @Transactional
    public LessonDTO publishLesson(String lessonId, Boolean isPublished) {
        log.info("Publishing lesson with ID: {}, status: {}", lessonId, isPublished);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with ID: " + lessonId));

        lesson.setIsPublished(isPublished);
        lesson.setUpdatedAt(LocalDateTime.now());

        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("Lesson published successfully with ID: {}", lessonId);

        return mapToDTO(savedLesson);
    }

    /**
     * Delete lesson
     */
    @Transactional
    public void deleteLesson(String lessonId) {
        log.info("Deleting lesson with ID: {}", lessonId);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with ID: " + lessonId));

        lessonRepository.deleteById(lessonId);
        log.info("Lesson deleted successfully with ID: {}", lessonId);
    }

    /**
     * Get lesson by ID
     */
    public LessonDTO getLessonById(String lessonId) {
        log.info("Fetching lesson with ID: {}", lessonId);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with ID: " + lessonId));

        return mapToDTO(lesson);
    }

    /**
     * Get lessons by course ID
     */
    public List<LessonDTO> getLessonsByCourseId(String courseId) {
        log.info("Fetching lessons for course: {}", courseId);

        // Verify course exists
        courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        return lessonRepository.findByCourseIdOrderByOrder(courseId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get published lessons by course ID
     */
    public List<LessonDTO> getPublishedLessonsByCourseId(String courseId) {
        log.info("Fetching published lessons for course: {}", courseId);

        return lessonRepository.findByCourseIdAndIsPublishedTrue(courseId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get lessons by course ID with pagination
     */
    public Page<LessonDTO> getLessonsByCourseIdPaged(String courseId, Pageable pageable) {
        log.info("Fetching lessons (paged) for course: {}", courseId);

        // Verify course exists
        courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        return lessonRepository.findByCourseId(courseId, pageable)
                .map(this::mapToDTO);
    }

    /**
     * Get lesson count by course ID
     */
    public Long getLessonCountByCourseId(String courseId) {
        log.info("Counting lessons for course: {}", courseId);
        return lessonRepository.countByCourseId(courseId);
    }

    // Private helper methods

    private LessonDTO mapToDTO(Lesson lesson) {
        return LessonDTO.builder()
                .id(lesson.getId())
                .courseId(lesson.getCourseId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .videoUrl(lesson.getVideoUrl())
                .order(lesson.getOrder())
                .duration(lesson.getDuration())
                .description(lesson.getDescription())
                .isPublished(lesson.getIsPublished())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }
}
