package com.eduflow.notification.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
