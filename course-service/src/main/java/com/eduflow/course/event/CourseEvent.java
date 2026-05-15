package com.eduflow.course.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseEvent {
    private String type;
    private String courseId;
    private String instructorId;
    private long timestamp;
}
