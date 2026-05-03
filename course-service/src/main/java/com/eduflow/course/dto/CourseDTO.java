package com.eduflow.course.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.LocalDateTime;
import java.util.List;

public class CourseDTO {
    private String id;

    @NotBlank(message = "Course title is required")
    private String title;

    @NotBlank(message = "Course description is required")
    private String description;

    @NotNull(message = "Instructor ID is required")
    private String instructorId;

    @NotNull(message = "Price is required")
    @Min(0)
    private Double price;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Level is required")
    private String level; // BEGINNER, INTERMEDIATE, ADVANCED

    @NotNull(message = "Duration is required")
    @Min(1)
    private Integer duration; // in hours

    private String thumbnail;
    private String instructorName;
    private Boolean isPublished;
    private List<String> lessonIds;
    private Double averageRating;
    private Integer totalReviews;
    private Integer enrollmentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public List<String> getLessonIds() {
        return lessonIds;
    }

    public void setLessonIds(List<String> lessonIds) {
        this.lessonIds = lessonIds;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Integer getEnrollmentCount() {
        return enrollmentCount;
    }

    public void setEnrollmentCount(Integer enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
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

    public CourseDTO() {}

    public CourseDTO(String id, String title, String description, String instructorId, String instructorName, Double price, String category, String level, Integer duration, String thumbnail, Boolean isPublished, List<String> lessonIds, Double averageRating, Integer totalReviews, Integer enrollmentCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.price = price;
        this.category = category;
        this.level = level;
        this.duration = duration;
        this.thumbnail = thumbnail;
        this.isPublished = isPublished;
        this.lessonIds = lessonIds;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
        this.enrollmentCount = enrollmentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CourseDTOBuilder builder() {
        return new CourseDTOBuilder();
    }
    
    public static class CourseDTOBuilder {
        private String id; private String title; private String description; private String instructorId; private String instructorName; private Double price; private String category; private String level; private Integer duration; private String thumbnail; private Boolean isPublished; private List<String> lessonIds; private Double averageRating; private Integer totalReviews; private Integer enrollmentCount; private LocalDateTime createdAt; private LocalDateTime updatedAt;
        
        public CourseDTOBuilder id(String id) {
            this.id = id;
            return this;
        }

        public CourseDTOBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CourseDTOBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CourseDTOBuilder instructorId(String instructorId) {
            this.instructorId = instructorId;
            return this;
        }

        public CourseDTOBuilder price(Double price) {
            this.price = price;
            return this;
        }

        public CourseDTOBuilder category(String category) {
            this.category = category;
            return this;
        }

        public CourseDTOBuilder level(String level) {
            this.level = level;
            return this;
        }

        public CourseDTOBuilder duration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public CourseDTOBuilder thumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public CourseDTOBuilder instructorName(String instructorName) {
            this.instructorName = instructorName;
            return this;
        }

        public CourseDTOBuilder isPublished(Boolean isPublished) {
            this.isPublished = isPublished;
            return this;
        }

        public CourseDTOBuilder lessonIds(List<String> lessonIds) {
            this.lessonIds = lessonIds;
            return this;
        }

        public CourseDTOBuilder averageRating(Double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public CourseDTOBuilder totalReviews(Integer totalReviews) {
            this.totalReviews = totalReviews;
            return this;
        }

        public CourseDTOBuilder enrollmentCount(Integer enrollmentCount) {
            this.enrollmentCount = enrollmentCount;
            return this;
        }

        public CourseDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CourseDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public CourseDTO build() {
            return new CourseDTO(id, title, description, instructorId, instructorName, price, category, level, duration, thumbnail, isPublished, lessonIds, averageRating, totalReviews, enrollmentCount, createdAt, updatedAt);
        }
    }
}
