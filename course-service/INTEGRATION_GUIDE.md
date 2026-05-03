# Hướng dẫn Tích Hợp Course Service vào EduFlow

## 📝 Bước 1: Copy Folder Course Service

1. Copy toàn bộ folder `course-service` vào thư mục gốc của dự án:
```bash
eduflow/
├── api-gateway/
├── config-server/
├── eureka-server/
├── identity-service/
├── course-service/          # ← Copy vào đây
├── enrollment-service/      # Sẽ triển khai giai đoạn tiếp theo
└── pom.xml
```

## 🔧 Bước 2: Cập Nhật Parent pom.xml

Thêm module `course-service` vào parent `pom.xml`:

```xml
<modules>
    <module>eureka-server</module>
    <module>api-gateway</module>
    <module>identity-service</module>
    <module>config-server</module>
    <module>course-service</module>        <!-- Thêm dòng này -->
    <module>enrollment-service</module>
    <module>notification-service</module>
</modules>
```

## 🐳 Bước 3: Cập Nhật Docker Compose

Thêm service `course-service` vào `docker/docker-compose.yml`:

```yaml
  # Course Service
  course-service:
    build:
      context: ../course-service
      dockerfile: Dockerfile
    container_name: course-service
    ports:
      - "8082:8082"
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://mongo:27017/course_db
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka
    depends_on:
      - mongo
      - redis
      - kafka
      - eureka-server
    networks:
      - eduflow-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8082/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
```

## 📦 Bước 4: Cài Đặt Dependencies

Chạy lệnh Maven để cài đặt dependencies:

```bash
# Build toàn bộ project
mvn clean install

# Hoặc chỉ build course-service
mvn clean package -pl course-service
```

## 🚀 Bước 5: Chạy Course Service

### Option 1: Chạy với Docker Compose (Recommended)

```bash
cd docker
docker-compose up -d

# Kiểm tra logs
docker-compose logs -f course-service

# Kiểm tra service đã register với Eureka?
curl http://localhost:8761/eureka/apps
```

### Option 2: Chạy Local (Development)

Đảm bảo MongoDB, Redis, Kafka, Eureka Server đã chạy:

```bash
# Terminal 1: Start Eureka Server
cd eureka-server
mvn spring-boot:run

# Terminal 2: Start MongoDB
mongod

# Terminal 3: Start Redis
redis-server

# Terminal 4: Start Kafka
kafka-server-start.sh config/server.properties

# Terminal 5: Start Course Service
cd course-service
mvn spring-boot:run
```

## ✅ Bước 6: Verification

### 1. Kiểm tra Service Discovery
```bash
curl http://localhost:8761/eureka/apps/COURSE-SERVICE
```

Kết quả mong đợi: Service đã register thành công

### 2. Kiểm tra Health Check
```bash
curl http://localhost:8082/actuator/health
```

Kết quả mong đợi:
```json
{
  "status": "UP"
}
```

### 3. Kiểm tra API
```bash
# Get featured courses
curl http://localhost:8080/api/v1/courses/featured

# Create course (thêm X-User-Id header)
curl -X POST http://localhost:8080/api/v1/courses \
  -H "Content-Type: application/json" \
  -H "X-User-Id: instructor-123" \
  -d '{
    "title": "Java Programming",
    "description": "Learn Java",
    "price": 29.99,
    "category": "Programming",
    "level": "BEGINNER",
    "duration": 40
  }'
```

### 4. Kiểm tra MongoDB
```bash
# Kết nối vào MongoDB
mongosh

# Chuyển sang course_db
use course_db

# Xem danh sách collections
show collections

# Xem dữ liệu courses
db.courses.find()
```

### 5. Kiểm tra Logs
```bash
# Docker
docker-compose logs -f course-service

# Local
# Logs sẽ ở file: logs/course-service.log
tail -f logs/course-service.log
```

## 🔗 Bước 7: Tích Hợp với API Gateway

API Gateway sẽ tự động định tuyến requests đến Course Service nhờ Service Discovery.

Kiểm tra route đã hoạt động:
```bash
# Thông qua API Gateway
curl http://localhost:8080/api/v1/courses

# Trực tiếp từ Course Service
curl http://localhost:8082/api/v1/courses
```

## 🧪 Bước 8: Chạy Tests

```bash
# Unit tests
mvn test -pl course-service

# Integration tests
mvn verify -pl course-service

# Test coverage
mvn jacoco:report -pl course-service
```

## 📊 Monitoring

### Health Endpoint
```bash
curl http://localhost:8082/actuator/health
```

### Metrics Endpoint
```bash
curl http://localhost:8082/actuator/metrics
```

### Prometheus Metrics
```bash
curl http://localhost:8082/actuator/prometheus
```

## 🔄 Chuẩn Bị cho Enrollment Service (Giai đoạn tiếp theo)

Enrollment Service sẽ gọi Course Service để:
1. Kiểm tra khóa học tồn tại
2. Cập nhật số lượng đăng ký

Các endpoint được sử dụng:
```
GET /api/v1/courses/{courseId}/exists
PATCH /api/v1/courses/{courseId}/enrollment-count?increment={number}
```

## 🐛 Troubleshooting

### Course Service không chạy được
```bash
# Check if port 8082 is already in use
lsof -i :8082

# Kill process
kill -9 <PID>
```

### MongoDB connection error
```bash
# Verify MongoDB is running
mongosh

# Check connection string in application.yml
# Default: mongodb://localhost:27017/course_db
```

### Service not registered with Eureka
```bash
# Check Eureka Server logs
docker-compose logs eureka-server

# Verify eureka.client.service-url in application.yml
```

### Redis connection error
```bash
# Verify Redis is running
redis-cli ping

# Should return: PONG
```

## 📝 Các File Chính

| File | Mô Tả |
|------|-------|
| `pom.xml` | Maven dependencies |
| `src/main/java/com/eduflow/course/CourseServiceApplication.java` | Main application class |
| `src/main/java/com/eduflow/course/controller/` | REST Controllers |
| `src/main/java/com/eduflow/course/service/` | Business logic |
| `src/main/java/com/eduflow/course/entity/` | MongoDB entities |
| `src/main/java/com/eduflow/course/repository/` | Data access layer |
| `src/main/resources/application.yml` | Configuration |
| `Dockerfile` | Docker image |

## 🎯 Success Criteria

✅ Course Service đã được setup thành công nếu:
1. Service tạo được course mới
2. Course có thể được cập nhật, công bố, xóa
3. Danh sách course có thể được lấy với phân trang
4. Featured courses được cache trong Redis
5. MongoDB lưu trữ dữ liệu course
6. Kafka publish event khi course được công bố
7. Service Discovery nhìn thấy service trong Eureka
8. Health check trả về status UP

## 📞 Next Steps

Sau khi Course Service hoạt động bình thường, bước tiếp theo là:
1. **Giai đoạn 4**: Enrollment Service - Quản lý đăng ký khóa học
2. **Giai đoạn 5**: Notification Service - Gửi email/thông báo
3. **Giai đoạn 6-10**: Testing, Documentation, Deployment

---

**Ghi chú**: Đảm bảo tất cả dependencies đã được cài đặt và toàn bộ infrastructure (MongoDB, Redis, Kafka, Eureka) đang chạy trước khi bắt đầu Course Service.
