package com.eduflow.notification.consumer;

import com.eduflow.notification.event.EnrollmentEvent;
import com.eduflow.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationService notificationService;

    @KafkaListener(topics = "enrollment-events", groupId = "notification-service-group")
    public void handleEnrollmentEvent(EnrollmentEvent event) {
        log.info("Received enrollment event: {}", event);
        
        if ("ENROLLMENT_CREATED".equals(event.getEventType())) {
            String title = "Chào mừng bạn đến với khóa học!";
            String message = String.format("Bạn đã đăng ký thành công khóa học với ID: %s. Chúc bạn học tập tốt!", event.getCourseId());
            
            notificationService.sendNotification(
                    event.getUserId(),
                    title,
                    message,
                    "EMAIL"
            );
        }
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public NotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
