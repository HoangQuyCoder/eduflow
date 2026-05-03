package com.eduflow.notification.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
