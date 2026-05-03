package com.eduflow.notification.consumer;

import com.eduflow.notification.event.EnrollmentEvent;
import com.eduflow.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

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

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NotificationConsumer.class);

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public NotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
