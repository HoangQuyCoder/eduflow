package com.eduflow.course.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "course_ratings")
@Getter
@Setter
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
