package com.eduflow.course.service;

import com.eduflow.course.dto.CourseDTO;
import com.eduflow.course.entity.Course;
import com.eduflow.course.entity.Lesson;
import com.eduflow.course.event.CourseEvent;
import com.eduflow.course.exception.CourseNotFoundException;
import com.eduflow.course.exception.UnauthorizedException;
import com.eduflow.course.repository.CourseRepository;
import com.eduflow.course.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.eduflow.course.dto.external.UserDTO;
import com.eduflow.course.feign.IdentityClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final IdentityClient identityClient;

    private static final String FEATURED_COURSES_CACHE_KEY = "featured:courses";
    private static final long CACHE_TTL = 3600; // 1 hour in seconds

    /**
     * Create a new course
     */
    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO, String instructorId) {
        log.info("Creating course with title: {} for instructor: {}", courseDTO.getTitle(), instructorId);

        Course course = Course.builder()
                .title(courseDTO.getTitle())
                .description(courseDTO.getDescription())
                .instructorId(instructorId)
                .price(courseDTO.getPrice())
                .category(courseDTO.getCategory())
                .level(courseDTO.getLevel())
                .duration(courseDTO.getDuration())
                .thumbnail(courseDTO.getThumbnail())
                .isPublished(false)
                .enrollmentCount(0)
                .totalReviews(0)
                .averageRating(0.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Course savedCourse = courseRepository.save(course);
        log.info("Course created successfully with ID: {}", savedCourse.getId());

        return mapToDTO(savedCourse);
    }

    /**
     * Update course details
     */
    @Transactional
    public CourseDTO updateCourse(String courseId, CourseDTO courseDTO, String instructorId) {
        log.info("Updating course with ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        // Check if user is the instructor
        if (!course.getInstructorId().equals(instructorId)) {
            throw new UnauthorizedException("Only instructor can update this course");
        }

        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setPrice(courseDTO.getPrice());
        course.setCategory(courseDTO.getCategory());
        course.setLevel(courseDTO.getLevel());
        course.setDuration(courseDTO.getDuration());
        course.setThumbnail(courseDTO.getThumbnail());
        course.setUpdatedAt(LocalDateTime.now());

        Course updatedCourse = courseRepository.save(course);

        // Clear cache
        clearFeaturedCoursesCache();

        log.info("Course updated successfully with ID: {}", courseId);
        return mapToDTO(updatedCourse);
    }

    /**
     * Publish or unpublish a course
     */
    @Transactional
    public CourseDTO publishCourse(String courseId, Boolean isPublished, String instructorId) {
        log.info("Publishing course with ID: {}, status: {}", courseId, isPublished);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        if (!course.getInstructorId().equals(instructorId)) {
            throw new UnauthorizedException("Only instructor can publish this course");
        }

        course.setIsPublished(isPublished);
        course.setUpdatedAt(LocalDateTime.now());

        Course savedCourse = courseRepository.save(course);

        // Publish event to Kafka
        if (isPublished) {
            publishCourseEvent(courseId, "COURSE_PUBLISHED");
        }

        // Clear cache
        clearFeaturedCoursesCache();

        log.info("Course published successfully with ID: {}", courseId);
        return mapToDTO(savedCourse);
    }

    /**
     * Delete a course
     */
    @Transactional
    public void deleteCourse(String courseId, String instructorId) {
        log.info("Deleting course with ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        if (!course.getInstructorId().equals(instructorId)) {
            throw new UnauthorizedException("Only instructor can delete this course");
        }

        // Delete all lessons
        lessonRepository.deleteByCourseId(courseId);

        // Delete course
        courseRepository.deleteById(courseId);

        // Clear cache
        clearFeaturedCoursesCache();

        log.info("Course deleted successfully with ID: {}", courseId);
    }

    /**
     * Get course by ID (published only)
     */
    public CourseDTO getCourseById(String courseId) {
        log.info("Fetching course with ID: {}", courseId);

        Course course = courseRepository.findByIdAndIsPublishedTrue(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        // Fetch lessons
        List<Lesson> lessons = lessonRepository.findByCourseIdAndIsPublishedTrue(courseId);
        course.setLessonIds(lessons.stream().map(Lesson::getId).collect(Collectors.toList()));

        return mapToDTO(course);
    }

    /**
     * Get course by ID (for instructor/admin)
     */
    public CourseDTO getCourseByIdInternal(String courseId) {
        log.info("Fetching course (internal) with ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        return mapToDTO(course);
    }

    /**
     * Get all published courses with pagination
     */
    public Page<CourseDTO> getAllCourses(Pageable pageable) {
        log.info("Fetching all published courses");

        return courseRepository.findByIsPublishedTrue(pageable)
                .map(this::mapToDTO);
    }

    /**
     * Get courses by category
     */
    public Page<CourseDTO> getCoursesByCategory(String category, Pageable pageable) {
        log.info("Fetching courses by category: {}", category);

        return courseRepository.findByCategoryAndIsPublishedTrue(category, pageable)
                .map(this::mapToDTO);
    }

    /**
     * Get courses by level
     */
    public Page<CourseDTO> getCoursesByLevel(String level, Pageable pageable) {
        log.info("Fetching courses by level: {}", level);

        return courseRepository.findByLevelAndIsPublishedTrue(level, pageable)
                .map(this::mapToDTO);
    }

    /**
     * Search courses by title
     */
    public Page<CourseDTO> searchCourses(String title, Pageable pageable) {
        log.info("Searching courses with title: {}", title);

        return courseRepository.findByTitleContainingIgnoreCaseAndIsPublishedTrue(title, pageable)
                .map(this::mapToDTO);
    }

    /**
     * Get featured courses (cached)
     */
    @SuppressWarnings("unchecked")
    public List<CourseDTO> getFeaturedCourses() {
        log.info("Fetching featured courses");

        // Try to get from cache
        Object cached = redisTemplate.opsForValue().get(FEATURED_COURSES_CACHE_KEY);
        if (cached != null) {
            log.info("Featured courses found in cache");
            return (List<CourseDTO>) cached;
        }

        // Get from database
        List<CourseDTO> featuredCourses = courseRepository
                .findByIsPublishedTrueOrderByAverageRatingDescEnrollmentCountDesc()
                .stream()
                .limit(10)
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        // Cache the result
        redisTemplate.opsForValue().set(FEATURED_COURSES_CACHE_KEY, featuredCourses, CACHE_TTL, TimeUnit.SECONDS);
        log.info("Featured courses cached");

        return featuredCourses;
    }

    /**
     * Get courses by instructor
     */
    public Page<CourseDTO> getCoursesByInstructor(String instructorId, Pageable pageable) {
        log.info("Fetching courses by instructor: {}", instructorId);

        return courseRepository.findByInstructorIdAndIsPublishedTrue(instructorId, pageable)
                .map(this::mapToDTO);
    }

    /**
     * Get all courses by instructor (including unpublished)
     */
    public List<CourseDTO> getAllCoursesByInstructorInternal(String instructorId) {
        log.info("Fetching all courses (internal) by instructor: {}", instructorId);

        return courseRepository.findByInstructorId(instructorId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Check if course exists
     */
    public Boolean courseExists(String courseId) {
        return courseRepository.existsById(courseId);
    }

    /**
     * Update enrollment count
     */
    @Transactional
    public void updateEnrollmentCount(String courseId, Integer increment) {
        log.info("Updating enrollment count for course: {} with increment: {}", courseId, increment);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        course.setEnrollmentCount(course.getEnrollmentCount() + increment);
        courseRepository.save(course);

        // Clear cache
        clearFeaturedCoursesCache();
    }

    /**
     * Update average rating
     */
    @Transactional
    public void updateAverageRating(String courseId, Double newAverage, Integer totalReviews) {
        log.info("Updating average rating for course: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        course.setAverageRating(newAverage);
        course.setTotalReviews(totalReviews);
        courseRepository.save(course);

        // Clear cache
        clearFeaturedCoursesCache();
    }

    // Private helper methods

    private CourseDTO mapToDTO(Course course) {
        String instructorName = "Unknown";
        try {
            UserDTO instructor = identityClient.getUser(course.getInstructorId());
            if (instructor != null && instructor.getFullName() != null) {
                instructorName = instructor.getFullName();
            }
        } catch (Exception e) {
            log.warn("Could not fetch instructor name for ID: {}", course.getInstructorId());
        }

        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .instructorId(course.getInstructorId())
                .instructorName(instructorName)
                .price(course.getPrice())
                .category(course.getCategory())
                .level(course.getLevel())
                .duration(course.getDuration())
                .thumbnail(course.getThumbnail())
                .isPublished(course.getIsPublished())
                .lessonIds(course.getLessonIds())
                .averageRating(course.getAverageRating())
                .totalReviews(course.getTotalReviews())
                .enrollmentCount(course.getEnrollmentCount())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    private void clearFeaturedCoursesCache() {
        redisTemplate.delete(FEATURED_COURSES_CACHE_KEY);
        log.info("Featured courses cache cleared");
    }

    private void publishCourseEvent(String courseId, String eventType) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        CourseEvent event = new CourseEvent(
                courseId,
                course.getInstructorId(),
                eventType,
                System.currentTimeMillis());
        try {
            kafkaTemplate.send("course-events", event);
        } catch (Exception e) {
            log.error("Error publishing course event", e);
            throw new RuntimeException(e);
        }
    }

}

