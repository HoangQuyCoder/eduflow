package com.eduflow.enrollment.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private UUID id;
    private String title;
    private BigDecimal price;
    private String instructorName;
}

