package com.eduflow.enrollment.dto;

import lombok.*;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for course enrollment")
public class EnrollmentRequest {
    @Schema(description = "Unique identifier of the course to enroll in", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID courseId;
}

