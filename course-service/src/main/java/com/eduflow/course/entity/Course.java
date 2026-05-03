package com.eduflow.course.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
