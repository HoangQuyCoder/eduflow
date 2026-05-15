package com.eduflow.notification.service;

import com.eduflow.notification.entity.Notification;
import com.eduflow.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public void sendNotification(UUID recipientId, String title, String message, String type) {
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .title(title)
                .message(message)
                .type(type)
                .status("SENT")
                .createdAt(LocalDateTime.now())
                .sentAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        log.info("Sent {} notification to user {}: {}", type, recipientId, title);
    }

    public List<Notification> getNotificationsForUser(UUID userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }
}
