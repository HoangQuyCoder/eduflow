package com.eduflow.course.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRatingDTO {
    private String id;

    @NotBlank(message = "Course ID is required")
    private String courseId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Rating is required")
    @Min(1)
    @Max(5)
    private Integer rating;

    private String review;
    private LocalDateTime createdAt;
}
