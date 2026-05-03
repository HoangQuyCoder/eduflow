package com.eduflow.notification.service;

import com.eduflow.notification.entity.Notification;
import com.eduflow.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

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

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NotificationService.class);

    public NotificationRepository getNotificationRepository() {
        return notificationRepository;
    }

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
}
