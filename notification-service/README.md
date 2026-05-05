# Notification Service

Dịch vụ xử lý và gửi thông báo cho người dùng EduFlow.

## 🛠 Tính Năng
- Tiêu thụ sự kiện (Consume events) từ Kafka (từ Enrollment Service, Course Service).
- Lưu trữ lịch sử thông báo.
- Gửi thông báo thời gian thực (Có thể mở rộng thêm Email, Push Notification).

## 🏗 Công Nghệ
- **Database**: MongoDB (Lưu trữ document thông báo).
- **Messaging**: Kafka Consumer.
- **Framework**: Spring Boot 3.2.x.

## 📡 API Endpoints
- `GET /api/v1/notifications/user/{userId}`: Lấy lịch sử thông báo của người dùng.
- `PATCH /api/v1/notifications/{id}/read`: Đánh dấu đã đọc.

## 📖 Kafka Consumers
Lắng nghe các topic:
- `enrollment-events`: Thông báo khi đăng ký khóa học thành công.
- `course-events`: Thông báo khi có khóa học mới hoặc bài giảng mới.

## ⚙️ Cấu Hình
- `MONGO_URI`: Kết nối MongoDB.
- `KAFKA_BOOTSTRAP_SERVERS`: Danh sách Kafka brokers.
