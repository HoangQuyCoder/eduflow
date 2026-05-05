package com.eduflow.enrollment.dto;

import java.math.BigDecimal;
import java.util.UUID;


public class CourseDTO {
    private UUID id;
    private String title;
    private BigDecimal price;
    private String instructorName;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public CourseDTO() {}

    public CourseDTO(UUID id, String title, BigDecimal price, String instructorName) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.instructorName = instructorName;
    }

    public static CourseDTOBuilder builder() {
        return new CourseDTOBuilder();
    }
    
    public static class CourseDTOBuilder {
        private UUID id; private String title; private BigDecimal price; private String instructorName;
        
        public CourseDTOBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public CourseDTOBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CourseDTOBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public CourseDTOBuilder instructorName(String instructorName) {
            this.instructorName = instructorName;
            return this;
        }

        public CourseDTO build() {
            return new CourseDTO(id, title, price, instructorName);
        }
    }
}
