package com.eduflow.course.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.LocalDateTime;

public class CourseRatingDTO {
    private String id;

    @NotBlank(message = "Course ID is required")
    private String courseId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Rating is required")
    @Min(1)
    @Max(5)
    private Integer rating;

    private String review;
    private LocalDateTime createdAt;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public CourseRatingDTO() {}

    public CourseRatingDTO(String id, String courseId, String userId, Integer rating, String review, LocalDateTime createdAt) {
        this.id = id;
        this.courseId = courseId;
        this.userId = userId;
        this.rating = rating;
        this.review = review;
        this.createdAt = createdAt;
    }

    public static CourseRatingDTOBuilder builder() {
        return new CourseRatingDTOBuilder();
    }
    
    public static class CourseRatingDTOBuilder {
        private String id; private String courseId; private String userId; private Integer rating; private String review; private LocalDateTime createdAt;
        
        public CourseRatingDTOBuilder id(String id) {
            this.id = id;
            return this;
        }

        public CourseRatingDTOBuilder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public CourseRatingDTOBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public CourseRatingDTOBuilder rating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public CourseRatingDTOBuilder review(String review) {
            this.review = review;
            return this;
        }

        public CourseRatingDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CourseRatingDTO build() {
            return new CourseRatingDTO(id, courseId, userId, rating, review, createdAt);
        }
    }
}
