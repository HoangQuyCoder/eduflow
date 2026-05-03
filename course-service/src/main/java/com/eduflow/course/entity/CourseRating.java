package com.eduflow.course.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "course_ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRating {
    @Id
    private String id;

    private String courseId;
    private String userId;
    private Integer rating; // 1-5
    private String review;

    @CreatedDate
    private LocalDateTime createdAt;
}
