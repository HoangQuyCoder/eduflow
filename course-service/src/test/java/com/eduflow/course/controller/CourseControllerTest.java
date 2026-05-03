package com.eduflow.course.controller;

import com.eduflow.course.dto.CourseDTO;
import com.eduflow.course.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    private CourseDTO courseDTO;
    private String courseId = "course-123";
    private String userId = "user-123";

    @BeforeEach
    void setUp() {
        courseDTO = CourseDTO.builder()
                .id(courseId)
                .title("Java Programming")
                .description("Learn Java basics")
                .instructorId(userId)
                .price(29.99)
                .category("Programming")
                .level("BEGINNER")
                .duration(40)
                .thumbnail("https://example.com/image.jpg")
                .isPublished(true)
                .enrollmentCount(10)
                .totalReviews(5)
                .averageRating(4.5)
                .build();
    }

    @Test
    void testCreateCourse() throws Exception {
        // Arrange
        when(courseService.createCourse(any(CourseDTO.class), anyString()))
                .thenReturn(courseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/courses")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.title").value("Java Programming"));
    }

    @Test
    void testGetCourseById() throws Exception {
        // Arrange
        when(courseService.getCourseById(courseId)).thenReturn(courseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses/{courseId}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.title").value("Java Programming"));
    }

    @Test
    void testGetAllCourses() throws Exception {
        // Arrange
        Page<CourseDTO> page = new PageImpl<>(Arrays.asList(courseDTO));
        when(courseService.getAllCourses(any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(courseId));
    }

    @Test
    void testSearchCourses() throws Exception {
        // Arrange
        Page<CourseDTO> page = new PageImpl<>(Arrays.asList(courseDTO));
        when(courseService.searchCourses(anyString(), any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses/search")
                .param("title", "Java")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateCourse() throws Exception {
        // Arrange
        when(courseService.updateCourse(anyString(), any(CourseDTO.class), anyString()))
                .thenReturn(courseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/courses/{courseId}", courseId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java Programming"));
    }

    @Test
    void testDeleteCourse() throws Exception {
        // Arrange
        doNothing().when(courseService).deleteCourse(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/courses/{courseId}", courseId)
                .header("X-User-Id", userId))
                .andExpect(status().isNoContent());
    }
}
