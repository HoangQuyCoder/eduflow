package com.eduflow.notification.event;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentEvent {
    private UUID enrollmentId;
    private UUID userId;
    private UUID courseId;
    private String eventType;
}
