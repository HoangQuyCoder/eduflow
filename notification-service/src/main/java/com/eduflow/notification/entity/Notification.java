package com.eduflow.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    private String id;
    private UUID recipientId;
    private String title;
    private String message;
    private String type; // EMAIL, IN_APP
    private String status; // SENT, PENDING, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    
    // Additional metadata if needed
    private String metadata;
}
