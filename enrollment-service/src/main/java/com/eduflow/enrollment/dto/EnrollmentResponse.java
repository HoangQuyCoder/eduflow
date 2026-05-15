package com.eduflow.enrollment.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private UUID id;
    private UUID userId;
    private UUID courseId;
    private LocalDateTime enrolledAt;
    private String status;
    private Integer progressPercent;
}

