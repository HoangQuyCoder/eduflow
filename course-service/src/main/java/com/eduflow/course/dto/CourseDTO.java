package com.eduflow.course.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private String id;

    @NotBlank(message = "Course title is required")
    private String title;

    @NotBlank(message = "Course description is required")
    private String description;

    @NotNull(message = "Instructor ID is required")
    private String instructorId;

    @NotNull(message = "Price is required")
    @Min(0)
    private Double price;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Level is required")
    private String level; // BEGINNER, INTERMEDIATE, ADVANCED

    @NotNull(message = "Duration is required")
    @Min(1)
    private Integer duration; // in hours

    private String thumbnail;
    private String instructorName;
    private Boolean isPublished;
    private List<String> lessonIds;
    private Double averageRating;
    private Integer totalReviews;
    private Integer enrollmentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
