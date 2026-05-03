package com.eduflow.notification.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "notifications")
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(UUID recipientId) {
        this.recipientId = recipientId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Notification() {}

    public Notification(String id, UUID recipientId, String title, String message, String type, String status, LocalDateTime createdAt, LocalDateTime sentAt, String metadata) {
        this.id = id;
        this.recipientId = recipientId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.metadata = metadata;
    }

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }
    
    public static class NotificationBuilder {
        private String id; private UUID recipientId; private String title; private String message; private String type; private String status; private LocalDateTime createdAt; private LocalDateTime sentAt; private String metadata;
        
        public NotificationBuilder id(String id) {
            this.id = id;
            return this;
        }

        public NotificationBuilder recipientId(UUID recipientId) {
            this.recipientId = recipientId;
            return this;
        }

        public NotificationBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NotificationBuilder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationBuilder type(String type) {
            this.type = type;
            return this;
        }

        public NotificationBuilder status(String status) {
            this.status = status;
            return this;
        }

        public NotificationBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public NotificationBuilder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public NotificationBuilder metadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        public Notification build() {
            return new Notification(id, recipientId, title, message, type, status, createdAt, sentAt, metadata);
        }
    }
}
