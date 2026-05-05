# Course Service

Dịch vụ quản lý nội dung học tập, bao gồm khóa học, bài học và hệ thống đánh giá cho EduFlow.

## 🛠 Tính Năng
- Quản lý khóa học (CRUD) và công bố nội dung.
- Quản lý bài học (Lessons) theo từng khóa học.
- Hệ thống đánh giá và nhận xét (Ratings & Reviews).
- Caching các khóa học nổi bật để tối ưu hiệu suất.
- Tìm kiếm và lọc khóa học theo danh mục, mức độ.

## 🏗 Công Nghệ
- **Database**: MongoDB (Lưu trữ tài liệu linh hoạt).
- **Caching**: Redis (Lưu trữ Featured Courses).
- **Messaging**: Kafka Producer (Gửi sự kiện khóa học mới).
- **Communication**: OpenFeign (Lấy thông tin giảng viên từ Identity Service).
- **Resilience**: Resilience4j Circuit Breaker.

## ⚙️ Cấu Hình
- `MONGO_URI`: Kết nối MongoDB.
- `REDIS_HOST`: Host của Redis cache.
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka broker list.
- `EUREKA_HOST`: URL của Discovery Service.

## 📡 API Endpoints

### Khóa Học (Courses)
- `GET /api/v1/courses`: Lấy danh sách khóa học (phân trang).
- `POST /api/v1/courses`: Tạo khóa học mới.
- `GET /api/v1/courses/{id}`: Chi tiết khóa học.
- `GET /api/v1/courses/featured`: Khóa học nổi bật (từ Cache).

### Bài Học (Lessons)
- `GET /api/v1/lessons/course/{courseId}`: Danh sách bài học của khóa học.
- `POST /api/v1/lessons`: Thêm bài học mới.
- `PUT /api/v1/lessons/{id}`: Cập nhật nội dung bài học.

### Đánh Giá (Ratings)
- `GET /api/v1/ratings/courses/{courseId}`: Lấy danh sách đánh giá.
- `POST /api/v1/ratings/courses/{courseId}`: Gửi đánh giá mới.

## 📖 Kafka Events
Publish event `course-events` khi có thay đổi trạng thái khóa học:
```json
{
  "courseId": "uuid",
  "instructorId": "uuid",
  "eventType": "COURSE_PUBLISHED",
  "timestamp": 123456789
}
```

## 🔍 Health Check
Truy cập: `http://localhost:8082/actuator/health`
