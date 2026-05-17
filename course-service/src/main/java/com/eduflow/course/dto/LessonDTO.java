package com.eduflow.course.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDTO {
    private String id;

    @NotBlank(message = "Course ID is required")
    private String courseId;

    @NotBlank(message = "Lesson title is required")
    private String title;

    @NotBlank(message = "Lesson content is required")
    private String content;

    private String videoUrl;

    @NotNull(message = "Lesson order is required")
    @Min(1)
    private Integer order;

    @NotNull(message = "Duration is required")
    @Min(1)
    private Integer duration; // in minutes

    private String description;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
