package com.eduflow.course.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "course_ratings")
public class CourseRating {
    @Id
    private String id;

    private String courseId;
    private String userId;
    private Integer rating; // 1-5
    private String review;

    @CreatedDate
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

    public CourseRating() {}

    public CourseRating(String id, String courseId, String userId, Integer rating, String review, LocalDateTime createdAt) {
        this.id = id;
        this.courseId = courseId;
        this.userId = userId;
        this.rating = rating;
        this.review = review;
        this.createdAt = createdAt;
    }

    public static CourseRatingBuilder builder() {
        return new CourseRatingBuilder();
    }
    
    public static class CourseRatingBuilder {
        private String id; private String courseId; private String userId; private Integer rating; private String review; private LocalDateTime createdAt;
        
        public CourseRatingBuilder id(String id) {
            this.id = id;
            return this;
        }

        public CourseRatingBuilder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public CourseRatingBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public CourseRatingBuilder rating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public CourseRatingBuilder review(String review) {
            this.review = review;
            return this;
        }

        public CourseRatingBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CourseRating build() {
            return new CourseRating(id, courseId, userId, rating, review, createdAt);
        }
    }
}
