package com.eduflow.notification.controller;

import com.eduflow.notification.entity.Notification;
import com.eduflow.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "Endpoints for retrieving user notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Get notifications by user ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }

    @Operation(summary = "Get my notifications", description = "Retrieves notifications for the currently authenticated user")
    @GetMapping("/me")
    public ResponseEntity<List<Notification>> getMyNotifications(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }
}
