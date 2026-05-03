# Course Service - EduFlow

Course Service là một microservice trong hệ thống EduFlow, quản lý tất cả các khóa học, bài học, và đánh giá khóa học.

## 🎯 Chức Năng Chính

- **Quản lý Khóa học**: Tạo, cập nhật, xóa, công bố khóa học
- **Quản lý Bài học**: Tạo, cập nhật, xóa các bài học trong khóa học
- **Đánh giá Khóa học**: Cho phép học viên đánh giá và viết nhận xét
- **Caching**: Redis cache cho danh sách khóa học nổi bật (TTL 1 giờ)
- **Pub/Sub Events**: Gửi sự kiện về Kafka khi khóa học được công bố
- **Service Discovery**: Tự động đăng ký với Eureka Server

## 📦 Công Nghệ Sử Dụng

- **Java 21 LTS**
- **Spring Boot 3.2.5**
- **Spring Cloud (Eureka Client)**
- **MongoDB 6.0+** - Lưu trữ dữ liệu khóa học
- **Redis 7.0+** - Caching
- **Apache Kafka 3.6+** - Event publishing
- **Spring Data MongoDB** - ORM cho MongoDB
- **Lombok** - Giảm boilerplate code
- **Resilience4j** - Circuit breaker, retry logic

## 🏗️ Cấu Trúc Thư Mục

```
course-service/
├── pom.xml
├── Dockerfile
├── src/
│   ├── main/
│   │   ├── java/com/eduflow/course/
│   │   │   ├── CourseServiceApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── CourseController.java
│   │   │   │   ├── LessonController.java
│   │   │   │   └── CourseRatingController.java
│   │   │   ├── service/
│   │   │   │   ├── CourseService.java
│   │   │   │   ├── LessonService.java
│   │   │   │   └── CourseRatingService.java
│   │   │   ├── entity/
│   │   │   │   ├── Course.java
│   │   │   │   ├── Lesson.java
│   │   │   │   └── CourseRating.java
│   │   │   ├── repository/
│   │   │   │   ├── CourseRepository.java
│   │   │   │   ├── LessonRepository.java
│   │   │   │   └── CourseRatingRepository.java
│   │   │   ├── dto/
│   │   │   │   ├── CourseDTO.java
│   │   │   │   ├── LessonDTO.java
│   │   │   │   └── CourseRatingDTO.java
│   │   │   └── exception/
│   │   │       ├── CourseExceptions.java
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       └── ErrorResponse.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/eduflow/course/
│           ├── service/
│           │   └── CourseServiceTest.java
│           └── controller/
│               └── CourseControllerTest.java
```

## 📋 Database Schema

### MongoDB Collections

#### `courses`
```
{
  _id: ObjectId,
  title: String,
  description: String,
  instructorId: String,
  price: Double,
  category: String,
  level: String,        // BEGINNER, INTERMEDIATE, ADVANCED
  duration: Integer,    // hours
  thumbnail: String,
  isPublished: Boolean,
  lessonIds: [String],
  averageRating: Double,
  totalReviews: Integer,
  enrollmentCount: Integer,
  createdAt: Date,
  updatedAt: Date
}
```

#### `lessons`
```
{
  _id: ObjectId,
  courseId: String,
  title: String,
  content: String,
  videoUrl: String,
  order: Integer,
  duration: Integer,    // minutes
  description: String,
  isPublished: Boolean,
  createdAt: Date,
  updatedAt: Date
}
```

#### `course_ratings`
```
{
  _id: ObjectId,
  courseId: String,
  userId: String,
  rating: Integer,      // 1-5
  review: String,
  createdAt: Date
}
```

## 🚀 Cài Đặt & Chạy

### Prerequisites
- Java 21 LTS
- MongoDB 6.0+
- Redis 7.0+
- Apache Kafka 3.6+
- Eureka Server (http://localhost:8761)

### Build Project
```bash
mvn clean package
```

### Run Service
```bash
java -jar target/course-service-1.0.0.jar
```

Hoặc sử dụng Spring Boot Maven plugin:
```bash
mvn spring-boot:run
```

### Docker
```bash
docker build -t course-service:1.0.0 .
docker run -p 8082:8082 \
  -e SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/course_db \
  -e SPRING_REDIS_HOST=localhost \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
  course-service:1.0.0
```

## 📡 REST API Endpoints

### Courses

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/courses` | Tạo khóa học mới |
| GET | `/api/v1/courses` | Lấy danh sách khóa học (có phân trang) |
| GET | `/api/v1/courses/{courseId}` | Lấy chi tiết khóa học |
| PUT | `/api/v1/courses/{courseId}` | Cập nhật khóa học |
| PATCH | `/api/v1/courses/{courseId}/publish` | Công bố/ẩn khóa học |
| DELETE | `/api/v1/courses/{courseId}` | Xóa khóa học |
| GET | `/api/v1/courses/featured` | Lấy khóa học nổi bật (cached) |
| GET | `/api/v1/courses/category/{category}` | Lấy khóa học theo danh mục |
| GET | `/api/v1/courses/level/{level}` | Lấy khóa học theo mức độ |
| GET | `/api/v1/courses/search?title=xxx` | Tìm kiếm khóa học |
| GET | `/api/v1/courses/instructor/{instructorId}` | Lấy khóa học của giảng viên |

### Lessons

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/lessons` | Tạo bài học mới |
| GET | `/api/v1/lessons/{lessonId}` | Lấy chi tiết bài học |
| PUT | `/api/v1/lessons/{lessonId}` | Cập nhật bài học |
| PATCH | `/api/v1/lessons/{lessonId}/publish` | Công bố/ẩn bài học |
| DELETE | `/api/v1/lessons/{lessonId}` | Xóa bài học |
| GET | `/api/v1/lessons/course/{courseId}` | Lấy danh sách bài học |
| GET | `/api/v1/lessons/course/{courseId}/published` | Lấy bài học đã công bố |
| GET | `/api/v1/lessons/course/{courseId}/paged` | Lấy bài học (có phân trang) |

### Ratings

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/ratings/courses/{courseId}` | Đánh giá khóa học |
| GET | `/api/v1/ratings/{ratingId}` | Lấy chi tiết đánh giá |
| DELETE | `/api/v1/ratings/{ratingId}` | Xóa đánh giá |
| GET | `/api/v1/ratings/courses/{courseId}` | Lấy danh sách đánh giá |
| GET | `/api/v1/ratings/courses/{courseId}/user` | Lấy đánh giá của người dùng |

## 🔐 Security

- **Headers Required**: 
  - `X-User-Id`: ID của người dùng (yêu cầu cho các endpoint cần xác thực)
  
- **Authorization**: 
  - Chỉ giảng viên/chủ sở hữu khóa học mới có thể cập nhật, xóa khóa học
  - Chỉ giảng viên mới có thể công bố khóa học

## 📊 Configuration

### application.yml
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/course_db
  redis:
    host: localhost
    port: 6379
  kafka:
    bootstrap-servers: localhost:9092

server:
  port: 8082

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Test Coverage
```bash
mvn jacoco:report
```

## 📚 API Examples

### Create Course
```bash
curl -X POST http://localhost:8082/api/v1/courses \
  -H "Content-Type: application/json" \
  -H "X-User-Id: instructor-123" \
  -d '{
    "title": "Advanced Java",
    "description": "Master Java programming",
    "price": 49.99,
    "category": "Programming",
    "level": "ADVANCED",
    "duration": 60,
    "thumbnail": "https://example.com/image.jpg"
  }'
```

### Get Courses
```bash
curl http://localhost:8082/api/v1/courses?page=0&size=10
```

### Create Lesson
```bash
curl -X POST http://localhost:8082/api/v1/lessons \
  -H "Content-Type: application/json" \
  -d '{
    "courseId": "course-123",
    "title": "Introduction to Java",
    "content": "Learn basics...",
    "videoUrl": "https://example.com/video.mp4",
    "order": 1,
    "duration": 30,
    "description": "First lesson"
  }'
```

### Rate Course
```bash
curl -X POST http://localhost:8082/api/v1/ratings/courses/course-123 \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-456" \
  -d '{
    "rating": 5,
    "review": "Excellent course!"
  }'
```

## 🔍 Health Check
```bash
curl http://localhost:8082/actuator/health
```

## 📖 Kafka Events

Service này publish các event sau:

### course-events topic
```json
{
  "courseId": "course-123",
  "instructorId": "instructor-123",
  "eventType": "COURSE_PUBLISHED",
  "timestamp": 1704067200000
}
```

## 🐛 Troubleshooting

1. **MongoDB Connection Error**
   - Kiểm tra MongoDB đang chạy: `mongosh`
   - Kiểm tra URI trong application.yml

2. **Redis Connection Error**
   - Kiểm tra Redis đang chạy: `redis-cli ping`
   - Kiểm tra host/port trong application.yml

3. **Kafka Connection Error**
   - Kiểm tra Kafka đang chạy
   - Kiểm tra bootstrap servers trong application.yml

4. **Service Not Registered with Eureka**
   - Kiểm tra Eureka Server đang chạy: http://localhost:8761
   - Kiểm tra eureka.client.service-url trong application.yml

## 📞 Support

Để báo cáo lỗi hoặc yêu cầu tính năng, vui lòng tạo issue trên GitHub.

## 📄 License

Apache License 2.0
