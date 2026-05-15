package com.eduflow.enrollment.event;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentEvent {
    private UUID enrollmentId;
    private UUID userId;
    private UUID courseId;
    private String eventType;
}

