package com.eduflow.course.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "courses")
public class Course {
    @Id
    private String id;

    private String title;
    private String description;

    @Indexed
    private String instructorId;

    private Double price;
    private String category;
    private String level; // BEGINNER, INTERMEDIATE, ADVANCED
    private Integer duration; // in hours
    private String thumbnail;

    @Indexed
    private Boolean isPublished;

    private List<String> lessonIds;
    private Double averageRating;
    private Integer totalReviews;
    private Integer enrollmentCount;

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

    public Course() {}

    public Course(String id, String title, String description, String instructorId, Double price, String category, String level, Integer duration, String thumbnail, Boolean isPublished, List<String> lessonIds, Double averageRating, Integer totalReviews, Integer enrollmentCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
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

    public static CourseBuilder builder() {
        return new CourseBuilder();
    }
    
    public static class CourseBuilder {
        private String id; private String title; private String description; private String instructorId; private Double price; private String category; private String level; private Integer duration; private String thumbnail; private Boolean isPublished; private List<String> lessonIds; private Double averageRating; private Integer totalReviews; private Integer enrollmentCount; private LocalDateTime createdAt; private LocalDateTime updatedAt;
        
        public CourseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public CourseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CourseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CourseBuilder instructorId(String instructorId) {
            this.instructorId = instructorId;
            return this;
        }

        public CourseBuilder price(Double price) {
            this.price = price;
            return this;
        }

        public CourseBuilder category(String category) {
            this.category = category;
            return this;
        }

        public CourseBuilder level(String level) {
            this.level = level;
            return this;
        }

        public CourseBuilder duration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public CourseBuilder thumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public CourseBuilder isPublished(Boolean isPublished) {
            this.isPublished = isPublished;
            return this;
        }

        public CourseBuilder lessonIds(List<String> lessonIds) {
            this.lessonIds = lessonIds;
            return this;
        }

        public CourseBuilder averageRating(Double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public CourseBuilder totalReviews(Integer totalReviews) {
            this.totalReviews = totalReviews;
            return this;
        }

        public CourseBuilder enrollmentCount(Integer enrollmentCount) {
            this.enrollmentCount = enrollmentCount;
            return this;
        }

        public CourseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CourseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Course build() {
            return new Course(id, title, description, instructorId, price, category, level, duration, thumbnail, isPublished, lessonIds, averageRating, totalReviews, enrollmentCount, createdAt, updatedAt);
        }
    }
}
