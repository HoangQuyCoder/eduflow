# Enrollment Service

Dịch vụ quản lý đăng ký khóa học và quy trình thanh toán.

## 🛠 Tính Năng
- Đăng ký người dùng vào khóa học.
- Kiểm tra trạng thái đăng ký.
- Xử lý đơn hàng và thanh toán (giả lập).
- Gửi sự kiện đăng ký thành công sang Kafka để thông báo.

## 🏗 Công Nghệ
- **Database**: PostgreSQL
- **Messaging**: Kafka Producer
- **Communication**: OpenFeign (gọi sang Course Service để kiểm tra tồn tại khóa học).
- **Resilience**: Resilience4j Circuit Breaker.

## 📡 API Endpoints
- `POST /api/v1/enrollments`: Đăng ký khóa học mới.
- `GET /api/v1/enrollments/user/{userId}`: Lấy danh sách khóa học người dùng đã đăng ký.
- `GET /api/v1/enrollments/course/{courseId}`: Kiểm tra đăng ký của khóa học.
- `POST /api/v1/payments`: Xử lý thanh toán cho đơn hàng.

## ⚙️ Cấu Hình
- `POSTGRES_HOST`: Database host.
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka broker list.
- `FEIGN_CLIENT_COURSE_URL`: URL đến Course Service.

## 📖 Kafka Events
Publish event `enrollment-events` với payload:
```json
{
  "enrollmentId": "uuid",
  "userId": "uuid",
  "courseId": "uuid",
  "status": "COMPLETED"
}
```
