package com.eduflow.course.service;

import com.eduflow.course.dto.CourseDTO;
import com.eduflow.course.entity.Course;
import com.eduflow.course.exception.CourseNotFoundException;
import com.eduflow.course.exception.UnauthorizedException;
import com.eduflow.course.repository.CourseRepository;
import com.eduflow.course.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private CourseService courseService;

    private CourseDTO courseDTO;
    private Course course;
    private String courseId = "course-123";
    private String instructorId = "instructor-123";

    @BeforeEach
    void setUp() {
        courseDTO = CourseDTO.builder()
                .title("Java Programming")
                .description("Learn Java basics")
                .price(29.99)
                .category("Programming")
                .level("BEGINNER")
                .duration(40)
                .thumbnail("https://example.com/image.jpg")
                .build();

        course = Course.builder()
                .id(courseId)
                .title("Java Programming")
                .description("Learn Java basics")
                .instructorId(instructorId)
                .price(29.99)
                .category("Programming")
                .level("BEGINNER")
                .duration(40)
                .thumbnail("https://example.com/image.jpg")
                .isPublished(false)
                .enrollmentCount(0)
                .totalReviews(0)
                .averageRating(0.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateCourse() {
        // Arrange
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        CourseDTO result = courseService.createCourse(courseDTO, instructorId);

        // Assert
        assertNotNull(result);
        assertEquals("Java Programming", result.getTitle());
        assertEquals(instructorId, result.getInstructorId());
        assertFalse(result.getIsPublished());
    }

    @Test
    void testUpdateCourse_Success() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(redisTemplate.delete(anyString())).thenReturn(true);

        // Act
        CourseDTO result = courseService.updateCourse(courseId, courseDTO, instructorId);

        // Assert
        assertNotNull(result);
        assertEquals("Java Programming", result.getTitle());
    }

    @Test
    void testUpdateCourse_Unauthorized() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            courseService.updateCourse(courseId, courseDTO, "other-instructor");
        });
    }

    @Test
    void testUpdateCourse_NotFound() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CourseNotFoundException.class, () -> {
            courseService.updateCourse(courseId, courseDTO, instructorId);
        });
    }

    @Test
    void testDeleteCourse_Success() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(lessonRepository).deleteByCourseId(courseId);
        doNothing().when(courseRepository).deleteById(courseId);
        when(redisTemplate.delete(anyString())).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> {
            courseService.deleteCourse(courseId, instructorId);
        });
    }

    @Test
    void testDeleteCourse_Unauthorized() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            courseService.deleteCourse(courseId, "other-instructor");
        });
    }

    @Test
    void testGetCourseById_Success() {
        // Arrange
        course.setIsPublished(true);
        when(courseRepository.findByIdAndIsPublishedTrue(courseId)).thenReturn(Optional.of(course));
        when(lessonRepository.findByCourseIdAndIsPublishedTrue(courseId)).thenReturn(java.util.Collections.emptyList());

        // Act
        CourseDTO result = courseService.getCourseById(courseId);

        // Assert
        assertNotNull(result);
        assertEquals(courseId, result.getId());
    }

    @Test
    void testGetCourseById_NotFound() {
        // Arrange
        when(courseRepository.findByIdAndIsPublishedTrue(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CourseNotFoundException.class, () -> {
            courseService.getCourseById(courseId);
        });
    }

    @Test
    void testCourseExists() {
        // Arrange
        when(courseRepository.existsById(courseId)).thenReturn(true);

        // Act
        Boolean exists = courseService.courseExists(courseId);

        // Assert
        assertTrue(exists);
    }
}
