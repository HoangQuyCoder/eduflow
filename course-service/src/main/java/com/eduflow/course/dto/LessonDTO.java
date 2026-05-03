package com.eduflow.course.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public class LessonDTO {
    private String id;

    @NotBlank(message = "Course ID is required")
    private String courseId;

    @NotBlank(message = "Lesson title is required")
    private String title;

    @NotBlank(message = "Lesson content is required")
    private String content;

    private String videoUrl;

    @NotNull(message = "Lesson order is required")
    @Min(1)
    private Integer order;

    @NotNull(message = "Duration is required")
    @Min(1)
    private Integer duration; // in minutes

    private String description;
    private Boolean isPublished;
    private LocalDateTime createdAt;
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

    public LessonDTO() {}

    public LessonDTO(String id, String courseId, String title, String content, String videoUrl, Integer order, Integer duration, String description, Boolean isPublished, LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    public static LessonDTOBuilder builder() {
        return new LessonDTOBuilder();
    }
    
    public static class LessonDTOBuilder {
        private String id; private String courseId; private String title; private String content; private String videoUrl; private Integer order; private Integer duration; private String description; private Boolean isPublished; private LocalDateTime createdAt; private LocalDateTime updatedAt;
        
        public LessonDTOBuilder id(String id) {
            this.id = id;
            return this;
        }

        public LessonDTOBuilder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public LessonDTOBuilder title(String title) {
            this.title = title;
            return this;
        }

        public LessonDTOBuilder content(String content) {
            this.content = content;
            return this;
        }

        public LessonDTOBuilder videoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
            return this;
        }

        public LessonDTOBuilder order(Integer order) {
            this.order = order;
            return this;
        }

        public LessonDTOBuilder duration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public LessonDTOBuilder description(String description) {
            this.description = description;
            return this;
        }

        public LessonDTOBuilder isPublished(Boolean isPublished) {
            this.isPublished = isPublished;
            return this;
        }

        public LessonDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public LessonDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public LessonDTO build() {
            return new LessonDTO(id, courseId, title, content, videoUrl, order, duration, description, isPublished, createdAt, updatedAt);
        }
    }
}
