package com.eduflow.enrollment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponse {
    private UUID id;
    private UUID userId;
    private UUID courseId;
    private LocalDateTime enrolledAt;
    private String status;
    private Integer progressPercent;
}
