package com.eduflow.notification.consumer;

import com.eduflow.notification.event.EnrollmentEvent;
import com.eduflow.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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
}
