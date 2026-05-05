package com.eduflow.course.exception;
public class LessonNotFoundException extends RuntimeException {
    public LessonNotFoundException(String message) { super(message); }
    public LessonNotFoundException(String message, Throwable cause) { super(message, cause); }
}
