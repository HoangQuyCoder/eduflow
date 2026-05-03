package com.eduflow.course.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "lessons")
public class Lesson {
    @Id
    private String id;

    private String courseId;
    private String title;
    private String content;
    private String videoUrl;
    private Integer order;
    private Integer duration; // in minutes
    private String description;
    private Boolean isPublished;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Lesson() {}

    public Lesson(String id, String courseId, String title, String content, String videoUrl, Integer order, Integer duration, String description, Boolean isPublished, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.content = content;
        this.videoUrl = videoUrl;
        this.order = order;
        this.duration = duration;
        this.description = description;
        this.isPublished = isPublished;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LessonBuilder builder() {
        return new LessonBuilder();
    }
    
    public static class LessonBuilder {
        private String id; private String courseId; private String title; private String content; private String videoUrl; private Integer order; private Integer duration; private String description; private Boolean isPublished; private LocalDateTime createdAt; private LocalDateTime updatedAt;
        
        public LessonBuilder id(String id) {
            this.id = id;
            return this;
        }

        public LessonBuilder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public LessonBuilder title(String title) {
            this.title = title;
            return this;
        }

        public LessonBuilder content(String content) {
            this.content = content;
            return this;
        }

        public LessonBuilder videoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
            return this;
        }

        public LessonBuilder order(Integer order) {
            this.order = order;
            return this;
        }

        public LessonBuilder duration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public LessonBuilder description(String description) {
            this.description = description;
            return this;
        }

        public LessonBuilder isPublished(Boolean isPublished) {
            this.isPublished = isPublished;
            return this;
        }

        public LessonBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public LessonBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Lesson build() {
            return new Lesson(id, courseId, title, content, videoUrl, order, duration, description, isPublished, createdAt, updatedAt);
        }
    }
}
