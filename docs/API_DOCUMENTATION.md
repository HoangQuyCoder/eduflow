# EduFlow - Hệ thống Quản lý Khóa học Online

## 📋 I. Tổng Quan Dự Án

### 1.1 Mô tả

EduFlow là một hệ thống quản lý học tập trực tuyến (LMS - Learning Management System) được xây dựng theo kiến trúc Microservices. Hệ thống cho phép:

- **Giảng viên**: Tạo khóa học, quản lý bài học, theo dõi tiến độ học viên
- **Học viên**: Đăng ký khóa học, xem nội dung bài học, theo dõi tiến độ cá nhân
- **Admin**: Quản lý hệ thống, người dùng, báo cáo

### 1.2 Mục tiêu kiến trúc

- Phân tách mối quan tâm: Mỗi service độc lập, có database riêng
- Khả năng mở rộng: Có thể scale từng service độc lập
- Tính linh hoạt: Giao tiếp bất đồng bộ giảm kết nối chặt chẽ
- Dễ bảo trì: Sửa lỗi hoặc update không ảnh hưởng đến service khác
- CI/CD tự động: Mỗi service có pipeline riêng

---

## 🏗️ II. Kiến Trúc Hệ Thống

### 2.1 Diagram Kiến Trúc Tổng Thể

```
┌─────────────────────────────────────────────────────────────┐
│                      Client Applications                     │
│              (Web, Mobile, Desktop, API Clients)             │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
        ┌────────────────────────────────────────┐
        │  API Gateway (Spring Cloud Gateway)    │
        │  - Authentication Check                │
        │  - Request Routing                     │
        │  - Rate Limiting                       │
        │  - Load Balancing                      │
        └────────┬─────────┬─────────┬──────────┘
                 │         │         │
        ┌────────▼──┐ ┌───▼────┐ ┌─▼──────────┐
        │ Identity  │ │ Course │ │ Enrollment │
        │ Service   │ │Service │ │  Service   │
        └────┬──────┘ └───┬────┘ └─┬──────────┘
             │            │        │
        ┌────▼────┐   ┌───▼───┐   ▼────────┐
        │PostgreSQL│  │MongoDB │  PostgreSQL│
        └──────────┘  └───────┘ └─────────┘
             │            │        │
        ┌────────────────────────────────────┐
        │  Notification Service (Async)      │
        │  - Email Sender                    │
        │  - Push Notification               │
        └────────────────────────────────────┘
             ▲            ▲        ▲
             └─────┬──────┴────┬───┘
                   │           │
             ┌─────▼───────────▼────┐
             │  Message Broker      │
             │  (Apache Kafka)      │
             └──────────────────────┘
                   ▲            │
        ┌──────────┘            └──────────┐
        │                                   │
   ┌────▼────────┐          ┌──────────────▼────┐
   │ Log & Trace │          │  Config Server    │
   │  (Zipkin)   │          │  (GitHub-hosted)  │
   └─────────────┘          └───────────────────┘
```

### 2.2 Danh sách các Microservices

#### **Service 1: Identity Service**

- **Port**: 8081
- **Mô tả**: Quản lý người dùng, xác thực (JWT), phân quyền (RBAC)
- **Chức năng chính**:
  - Đăng ký người dùng (Register)
  - Đăng nhập (Login) - Trả về JWT Token
  - Xác thực token
  - Phân quyền dựa trên Role (Admin, Teacher, Student)
  - Quản lý hồ sơ người dùng (Profile)
- **Database**: PostgreSQL (identity_db)
- **Tables**:
  - users (id, email, password_hash, full_name, role, created_at, updated_at)
  - user_roles (id, user_id, role, created_at)
  - user_profiles (id, user_id, avatar_url, bio, phone, updated_at)

#### **Service 2: Course Service**

- **Port**: 8082
- **Mô tả**: Quản lý khóa học, bài học, và nội dung học tập
- **Chức năng chính**:
  - Tạo khóa học (Create Course) - Chỉ Teacher/Admin
  - Cập nhật khóa học (Update Course)
  - Xóa khóa học (Delete Course)
  - Lấy danh sách khóa học (List Courses) - Với phân trang
  - Tạo bài học trong khóa học (Create Lesson)
  - Cập nhật, xóa bài học
  - Lấy chi tiết khóa học với danh sách bài học
  - Caching danh sách khóa học "nổi bật"
- **Database**: MongoDB (course_db)
- **Collections**:
  - courses (id, title, description, instructor_id, price, category, level, duration, is_published, created_at, updated_at)
  - lessons (id, course_id, title, content, video_url, order, duration, created_at, updated_at)
  - course_ratings (id, course_id, user_id, rating, review, created_at)
- **Cache**: Redis (Featured courses - TTL 1 hour)

#### **Service 3: Enrollment Service**

- **Port**: 8083
- **Mô tả**: Quản lý đăng ký khóa học, tiến độ học viên
- **Chức năng chính**:
  - Đăng ký khóa học (Enroll) - Kiểm tra khóa học tồn tại via Course Service
  - Hủy đăng ký (Unenroll)
  - Lấy danh sách khóa học của học viên
  - Cập nhật tiến độ bài học (Mark lesson as completed)
  - Lấy tiến độ học tập của học viên
  - Xử lý thanh toán (nếu khóa học có phí)
- **Database**: PostgreSQL (enrollment_db)
- **Tables**:
  - enrollments (id, user_id, course_id, enrolled_at, status, progress_percent, completed_at)
  - lesson_progress (id, enrollment_id, lesson_id, is_completed, completed_at, time_spent_minutes)
  - payments (id, user_id, course_id, amount, payment_method, status, transaction_id, created_at)

#### **Service 4: Notification Service**

- **Port**: 8084
- **Mô tả**: Gửi email, thông báo push, SMS (không có HTTP endpoint, nhận event từ Kafka)
- **Chức năng chính**:
  - Lắng nghe sự kiện: "User enrolled course" → Gửi email welcome
  - Lắng nghe sự kiện: "Course published" → Thông báo cho followers
  - Gửi email xác nhận đăng ký
  - Gửi email reminder khi có bài học mới
  - Lưu trữ lịch sử thông báo
- **Database**: PostgreSQL (notification_db)
- **Tables**:
  - notifications (id, user_id, title, message, type, read_status, created_at)
  - email_logs (id, recipient, subject, body, status, sent_at, error_message)
- **External Services**:
  - SMTP (Gmail hoặc SendGrid)
  - Firebase Cloud Messaging (FCM) cho push notification

#### **Service 5: API Gateway**

- **Port**: 8080
- **Mô tả**: Cửa ngõ duy nhất cho tất cả request từ client
- **Chức năng chính**:
  - Định tuyến request đến service tương ứng
  - Xác thực JWT token
  - Rate limiting
  - CORS configuration
  - Ghi log request/response
  - Circuit breaker cho các downstream service

#### **Service 6: Config Server (tuỳ chọn)**

- **Port**: 8888
- **Mô tả**: Tập trung lưu trữ cấu hình cho tất cả service
- **Lợi ích**: Thay đổi cấu hình mà không cần restart service
- **Repository**: GitHub repo `eduflow-config`

#### **Service 7: Service Discovery (Eureka Server)**

- **Port**: 8761
- **Mô tả**: Quản lý danh sách các instance của từng service đang chạy
- **Chức năng**:
  - Mỗi service tự đăng ký khi khởi động
  - API Gateway query danh sách này để biết địa chỉ service
  - Tự động remove service nếu heartbeat không còn

---

## 💻 III. Công Nghệ Sử Dụng

### 3.1 Backend Framework & Languages

| Công nghệ           | Phiên bản | Mục đích                    |
| ------------------- | --------- | --------------------------- |
| Java                | 21 LTS    | Ngôn ngữ lập trình          |
| Spring Boot         | 3.3.x     | Framework chính             |
| Spring Cloud        | 2023.0.x  | Hỗ trợ Microservices        |
| Spring Security     | 6.x       | Xác thực & phân quyền       |
| Spring Data JPA     | 3.x       | ORM cho database relational |
| Spring Data MongoDB | 3.x       | Driver cho MongoDB          |
| OpenFeign           | 4.1.x     | Gọi HTTP giữa services      |
| Resilience4j        | 2.x       | Circuit breaker, retry      |
| Micrometer Tracing  | 1.x       | Distributed tracing         |

### 3.2 Database

| Công nghệ  | Phiên bản | Service sử dụng                    | Mục đích                         |
| ---------- | --------- | ---------------------------------- | -------------------------------- |
| PostgreSQL | 15+       | Identity, Enrollment, Notification | RDBMS chính                      |
| MongoDB    | 6.0+      | Course                             | NoSQL cho dữ liệu không cấu trúc |
| Redis      | 7.0+      | Toàn hệ thống                      | Cache, session store             |

### 3.3 Message Broker & Event Streaming

| Công nghệ    | Phiên bản | Mục đích                                              |
| ------------ | --------- | ----------------------------------------------------- |
| Apache Kafka | 3.6+      | Message broker cho async communication                |
| Kafka Topics | -         | enrollment-events, course-events, notification-events |

### 3.4 Infrastructure & DevOps

| Công nghệ      | Phiên bản | Mục đích                            |
| -------------- | --------- | ----------------------------------- |
| Docker         | 24.x      | Containerization                    |
| Docker Compose | 2.x       | Orchestration cho local dev         |
| Kubernetes     | 1.28+     | Production orchestration (tuỳ chọn) |
| GitHub Actions | -         | CI/CD pipeline                      |
| Zipkin         | 2.x       | Distributed tracing visualization   |

### 3.5 Testing & Quality

| Công nghệ      | Phiên bản | Mục đích                                |
| -------------- | --------- | --------------------------------------- |
| JUnit 5        | 5.10.x    | Unit testing                            |
| Mockito        | 5.x       | Mocking framework                       |
| TestContainers | 1.x       | Integration testing với real containers |
| SonarQube      | -         | Code quality analysis                   |

### 3.6 Logging & Monitoring

| Công nghệ            | Mục đích               |
| -------------------- | ---------------------- |
| SLF4J + Logback      | Logging                |
| Spring Boot Actuator | Metrics & health check |
| Prometheus           | Metrics collection     |
| Grafana              | Visualization          |

---

## 📅 IV. Giai Đoạn Triển Khai Chi Tiết

### **Giai Đoạn 1: Chuẩn Bị & Cơ Sở Hạ Tầng (Tuần 1-2)**

#### Task 1.1: Tạo cấu trúc thư mục project

```
eduflow/
├── api-gateway/
├── identity-service/
├── course-service/
├── enrollment-service/
├── notification-service/
├── eureka-server/
├── config-server/
├── docker/
│   └── docker-compose.yml
├── kubernetes/ (tuỳ chọn)
├── .github/
│   └── workflows/
│       ├── identity-service.yml
│       ├── course-service.yml
│       └── ...
├── docs/
│   ├── API_DOCUMENTATION.md
│   ├── ARCHITECTURE.md
│   └── DEPLOYMENT_GUIDE.md
└── README.md
```

#### Task 1.2: Setup Eureka Server

- Tạo Spring Boot application với dependency `spring-cloud-starter-netflix-eureka-server`
- Cấu hình `application.yml`:
  ```yaml
  server:
    port: 8761
  eureka:
    client:
      register-with-eureka: false
      fetch-registry: false
    server:
      enable-self-preservation: false
  ```
- Dashboard URL: http://localhost:8761

#### Task 1.3: Setup Config Server (tuỳ chọn)

- Tạo Spring Boot application với `spring-cloud-starter-config-server`
- Repository GitHub: `eduflow-config`
- Cấu trúc config files:
  ```
  eduflow-config/
  ├── application.yml (chung)
  ├── identity-service.yml
  ├── course-service.yml
  ├── enrollment-service.yml
  └── notification-service.yml
  ```

---

### **Giai Đoạn 2: Identity Service (Tuần 2-3)**

#### Task 2.1: Thiết kế Database

- Tạo PostgreSQL database `identity_db`
- Tables:

  ```sql
  CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
  );

  CREATE TABLE user_roles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, role)
  );

  CREATE TABLE user_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    avatar_url VARCHAR(500),
    bio TEXT,
    phone VARCHAR(20),
    updated_at TIMESTAMP DEFAULT NOW()
  );
  ```

#### Task 2.2: Xây dựng API

- **Endpoints**:
  ```
  POST   /api/v1/auth/register      - Đăng ký người dùng
  POST   /api/v1/auth/login         - Đăng nhập, trả JWT
  POST   /api/v1/auth/refresh       - Refresh token
  POST   /api/v1/auth/logout        - Logout
  GET    /api/v1/auth/validate      - Xác thực token
  GET    /api/v1/users/{id}         - Lấy thông tin người dùng
  PUT    /api/v1/users/{id}         - Cập nhật thông tin
  GET    /api/v1/users              - Danh sách người dùng (Admin only)
  ```

#### Task 2.3: JWT Configuration

- Sử dụng Spring Security + JWT library (jjwt hoặc spring-security-oauth2)
- Config:
  - Secret key: 256-bit
  - Expiration: 24 hours (access token), 7 days (refresh token)
  - Claims: user_id, email, roles

#### Task 2.4: Role-Based Access Control (RBAC)

- Roles: ADMIN, TEACHER, STUDENT
- Custom annotation: `@RequireRole("TEACHER", "ADMIN")`
- SecurityConfig:
  ```java
  @EnableMethodSecurity
  @Configuration
  @EnableWebSecurity
  public class SecurityConfig {
      // JWT filter configuration
  }
  ```

#### Task 2.5: Testing

- Unit tests: Service layer
- Integration tests: API endpoints
- Test cases: Register, Login, Token validation, Authorization

#### Task 2.6: Docker Configuration

- Dockerfile
- Eureka client registration trong `application.yml`

---

### **Giai Đoạn 3: Course Service (Tuần 3-4)**

#### Task 3.1: Thiết kế Database (MongoDB)

- Database: `course_db`
- Collections:

  ```javascript
  // courses collection
  {
    _id: ObjectId,
    title: String,
    description: String,
    instructor_id: UUID,
    instructor_name: String,
    category: String,
    level: String, // BEGINNER, INTERMEDIATE, ADVANCED
    price: Decimal128,
    duration_hours: Number,
    thumbnail_url: String,
    is_published: Boolean,
    status: String, // DRAFT, PUBLISHED, ARCHIVED
    created_at: Date,
    updated_at: Date,
    tags: [String],
    prerequisites: [ObjectId]
  }

  // lessons collection
  {
    _id: ObjectId,
    course_id: ObjectId,
    title: String,
    description: String,
    content: String,
    video_url: String,
    order: Number,
    duration_minutes: Number,
    is_published: Boolean,
    created_at: Date,
    updated_at: Date,
    attachments: [
      {
        file_name: String,
        file_url: String,
        file_size: Number
      }
    ]
  }

  // course_ratings collection
  {
    _id: ObjectId,
    course_id: ObjectId,
    user_id: UUID,
    rating: Number, // 1-5
    review: String,
    created_at: Date,
    updated_at: Date
  }
  ```

#### Task 3.2: Xây dựng API

- **Endpoints**:

  ```
  GET    /api/v1/courses                     - Danh sách khóa học (với filter, sort, pagination)
  GET    /api/v1/courses/{id}                - Chi tiết khóa học
  POST   /api/v1/courses                     - Tạo khóa học (TEACHER/ADMIN)
  PUT    /api/v1/courses/{id}                - Cập nhật khóa học
  DELETE /api/v1/courses/{id}                - Xóa khóa học
  PATCH  /api/v1/courses/{id}/publish       - Xuất bản khóa học

  GET    /api/v1/courses/{id}/lessons       - Danh sách bài học
  POST   /api/v1/courses/{id}/lessons       - Tạo bài học
  PUT    /api/v1/lessons/{id}               - Cập nhật bài học
  DELETE /api/v1/lessons/{id}               - Xóa bài học

  GET    /api/v1/courses/{id}/ratings       - Xem đánh giá
  POST   /api/v1/courses/{id}/ratings       - Thêm đánh giá

  GET    /api/v1/courses/featured          - Khóa học nổi bật (from cache)
  GET    /api/v1/courses/popular           - Khóa học phổ biến
  ```

#### Task 3.3: Caching Strategy

- Redis key format: `featured_courses:lang_{language}`
- TTL: 1 hour
- Cache invalidation khi có course mới được publish
- Annotation: `@Cacheable`, `@CacheEvict`

#### Task 3.4: Pagination & Filtering

- Query parameters: page, size, sort, category, level, min_price, max_price, search
- Use Spring Data MongoDB Pageable interface

#### Task 3.5: Testing

- Unit tests: Service business logic
- Integration tests: API endpoints, MongoDB operations
- Performance test: Caching effectiveness

#### Task 3.6: Docker & Registration

- Dockerfile
- Register với Eureka Server

---

### **Giai Đoạn 4: API Gateway (Tuần 4)**

#### Task 4.1: Cấu hình Spring Cloud Gateway

- Route definitions:
  ```yaml
  spring:
    cloud:
      gateway:
        routes:
          - id: identity-service
            uri: lb://IDENTITY-SERVICE
            predicates:
              - Path=/api/v1/auth/**
          - id: course-service
            uri: lb://COURSE-SERVICE
            predicates:
              - Path=/api/v1/courses/**
          - id: enrollment-service
            uri: lb://ENROLLMENT-SERVICE
            predicates:
              - Path=/api/v1/enrollments/**
  ```

#### Task 4.2: Authentication Filter

- Custom GlobalFilter để xác thực JWT
- Nếu endpoint yêu cầu auth, kiểm tra token từ header `Authorization: Bearer <token>`
- Gọi Identity Service để validate token
- Thêm user info vào request header cho downstream services

#### Task 4.3: CORS Configuration

- Cho phép request từ client (web, mobile)
- Cấu hình methods, headers, credentials

#### Task 4.4: Rate Limiting (tuỳ chọn)

- Dùng Resilience4j hoặc Redis
- Giới hạn: N requests per minute per IP/User

#### Task 4.5: Circuit Breaker

- Khi một service bị down, gateway trả về fallback response thay vì timeout

---

### **Giai Đoạn 5: Enrollment Service (Tuần 5-6)**

#### Task 5.1: Thiết kế Database

- Database: PostgreSQL `enrollment_db`
- Tables:

  ```sql
  CREATE TABLE enrollments (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    course_id UUID NOT NULL,
    enrolled_at TIMESTAMP DEFAULT NOW(),
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, COMPLETED, DROPPED
    progress_percent INT DEFAULT 0,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, course_id)
  );

  CREATE TABLE lesson_progress (
    id UUID PRIMARY KEY,
    enrollment_id UUID NOT NULL REFERENCES enrollments(id),
    lesson_id UUID NOT NULL,
    is_completed BOOLEAN DEFAULT false,
    completed_at TIMESTAMP,
    time_spent_minutes INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(enrollment_id, lesson_id)
  );

  CREATE TABLE payments (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    course_id UUID NOT NULL,
    enrollment_id UUID NOT NULL REFERENCES enrollments(id),
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    payment_method VARCHAR(50), -- CREDIT_CARD, PAYPAL, BANK_TRANSFER
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, COMPLETED, FAILED, REFUNDED
    transaction_id VARCHAR(255),
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
  );

  CREATE INDEX idx_user_enrollments ON enrollments(user_id);
  CREATE INDEX idx_course_enrollments ON enrollments(course_id);
  ```

#### Task 5.2: Feign Client để gọi Course Service

```java
@FeignClient(name = "course-service", fallback = CourseFallback.class)
public interface CourseClient {
    @GetMapping("/api/v1/courses/{id}")
    CourseDTO getCourse(@PathVariable("id") String courseId);
}
```

#### Task 5.3: Circuit Breaker Configuration

```java
@CircuitBreaker(
    name = "courseService",
    fallback = "handleCourseServiceFailure"
)
public void enrollCourse(String courseId) { ... }
```

#### Task 5.4: Xây dựng API

- **Endpoints**:

  ```
  POST   /api/v1/enrollments                - Đăng ký khóa học
  DELETE /api/v1/enrollments/{id}           - Hủy đăng ký
  GET    /api/v1/enrollments                - Lấy khóa học của user (current user)
  GET    /api/v1/enrollments/{id}           - Chi tiết enrollment

  GET    /api/v1/enrollments/{id}/progress - Tiến độ chi tiết
  PUT    /api/v1/enrollments/{id}/lessons/{lessonId} - Mark lesson completed

  POST   /api/v1/payments                   - Initiate payment
  GET    /api/v1/payments/{id}              - Lấy thông tin thanh toán
  POST   /api/v1/payments/{id}/verify       - Verify payment từ gateway
  ```

#### Task 5.5: Publishing Events (Kafka)

```java
@Service
public class EnrollmentService {
    @Autowired
    private KafkaTemplate<String, EnrollmentEvent> kafkaTemplate;

    public void enrollCourse(EnrollmentRequest request) {
        // Create enrollment in DB
        Enrollment enrollment = enrollmentRepository.save(...);

        // Publish event
        EnrollmentEvent event = new EnrollmentEvent(
            enrollment.getId(),
            enrollment.getUserId(),
            enrollment.getCourseId(),
            "ENROLLMENT_CREATED"
        );
        kafkaTemplate.send("enrollment-events", event);
    }
}
```

#### Task 5.6: Event Structure

```java
@Data
public class EnrollmentEvent {
    private String enrollmentId;
    private UUID userId;
    private UUID courseId;
    private String eventType; // ENROLLMENT_CREATED, ENROLLMENT_COMPLETED, LESSON_COMPLETED
    private LocalDateTime timestamp;
}
```

#### Task 5.7: Testing

- Unit tests: Business logic
- Integration tests: API, database, Kafka publishing
- Load test: Concurrent enrollments

---

### **Giai Đoạn 6: Notification Service (Tuần 6-7)**

#### Task 6.1: Thiết kế Database

```sql
CREATE TABLE notifications (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  title VARCHAR(255),
  message TEXT,
  type VARCHAR(50), -- ENROLLMENT, COURSE_UPDATE, REMINDER
  read_status BOOLEAN DEFAULT false,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE email_logs (
  id UUID PRIMARY KEY,
  recipient VARCHAR(255) NOT NULL,
  subject VARCHAR(255),
  body TEXT,
  status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, SENT, FAILED
  sent_at TIMESTAMP,
  error_message TEXT,
  created_at TIMESTAMP DEFAULT NOW()
);
```

#### Task 6.2: Kafka Consumer Setup

```java
@Service
public class NotificationConsumer {
    @KafkaListener(topics = "enrollment-events", groupId = "notification-service")
    public void handleEnrollmentEvent(EnrollmentEvent event) {
        if ("ENROLLMENT_CREATED".equals(event.getEventType())) {
            sendWelcomeEmail(event.getUserId(), event.getCourseId());
        }
    }
}
```

#### Task 6.3: Email Service Implementation

- Sử dụng JavaMailSender (Spring)
- Template engine: Thymeleaf hoặc Freemarker
- Config SMTP:
  ```yaml
  spring:
    mail:
      host: smtp.gmail.com
      port: 587
      username: ${GMAIL_USERNAME}
      password: ${GMAIL_PASSWORD}
      properties:
        mail.smtp.auth: true
        mail.smtp.starttls.enable: true
  ```

#### Task 6.4: Email Templates

- Welcome email: Khi đăng ký khóa học
- Course update: Khi khóa học có bài học mới
- Reminder: Học viên chưa xem bài học trong N ngày
- Completion: Hoàn thành khóa học

#### Task 6.5: Testing

- Unit tests: Email content generation
- Integration tests: Kafka consumer, email sending

#### Task 6.6: No HTTP Endpoints

- Service này không có REST API
- Chỉ consume messages từ Kafka

---

### **Giai Đoạn 7: Distributed Tracing & Monitoring (Tuần 7)**

#### Task 7.1: Zipkin Setup

```yaml
# docker-compose.yml
zipkin:
  image: openzipkin/zipkin:latest
  ports:
    - "9411:9411"
```

#### Task 7.2: Micrometer Tracing Configuration

```yaml
management:
  tracing:
    sampling:
      probability: 1.0 # Trace 100% (development)
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

#### Task 7.3: Visualization

- Zipkin UI: http://localhost:9411
- Theo dõi request đi qua những service nào, latency tại mỗi service

---

### **Giai Đoạn 8: Docker Compose & Local Development (Tuần 8)**

#### Task 8.1: Tạo docker-compose.yml

```yaml
version: "3.8"

services:
  # Databases
  postgres-identity:
    image: postgres:15
    environment:
      POSTGRES_DB: identity_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_identity_data:/var/lib/postgresql/data

  postgres-enrollment:
    image: postgres:15
    environment:
      POSTGRES_DB: enrollment_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5433:5432"
    volumes:
      - postgres_enrollment_data:/var/lib/postgresql/data

  postgres-notification:
    image: postgres:15
    environment:
      POSTGRES_DB: notification_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5434:5432"
    volumes:
      - postgres_notification_data:/var/lib/postgresql/data

  mongodb:
    image: mongo:6
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: password
    volumes:
      - mongodb_data:/data/db

  redis:
    image: redis:7
    ports:
      - "6379:6379"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  zipkin:
    image: openzipkin/zipkin:latest
    ports:
      - "9411:9411"

  # Services
  eureka-server:
    build:
      context: ./eureka-server
      dockerfile: Dockerfile
    ports:
      - "8761:8761"
    environment:
      SPRING_PROFILES_ACTIVE: docker

  api-gateway:
    build:
      context: ./api-gateway
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    depends_on:
      - eureka-server
    environment:
      SPRING_PROFILES_ACTIVE: docker
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka

  identity-service:
    build:
      context: ./identity-service
      dockerfile: Dockerfile
    ports:
      - "8081:8081"
    depends_on:
      - postgres-identity
      - eureka-server
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres-identity:5432/identity_db
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka

  course-service:
    build:
      context: ./course-service
      dockerfile: Dockerfile
    ports:
      - "8082:8082"
    depends_on:
      - mongodb
      - redis
      - eureka-server
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATA_MONGODB_URI: mongodb://root:password@mongodb:27017/course_db
      SPRING_REDIS_HOST: redis
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka

  enrollment-service:
    build:
      context: ./enrollment-service
      dockerfile: Dockerfile
    ports:
      - "8083:8083"
    depends_on:
      - postgres-enrollment
      - kafka
      - eureka-server
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres-enrollment:5432/enrollment_db
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:29092
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka

  notification-service:
    build:
      context: ./notification-service
      dockerfile: Dockerfile
    ports:
      - "8084:8084"
    depends_on:
      - postgres-notification
      - kafka
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres-notification:5432/notification_db
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:29092
      MAIL_USERNAME: ${MAIL_USERNAME}
      MAIL_PASSWORD: ${MAIL_PASSWORD}

volumes:
  postgres_identity_data:
  postgres_enrollment_data:
  postgres_notification_data:
  mongodb_data:
```

#### Task 8.2: Dockerfile Template cho mỗi service

```dockerfile
# Base image
FROM openjdk:21-slim

# Working directory
WORKDIR /app

# Copy jar file
COPY target/service-name.jar app.jar

# Expose port
EXPOSE 8081

# Run application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
```

#### Task 8.3: Hướng dẫn chạy local

```bash
# 1. Build tất cả service
docker-compose build

# 2. Khởi chạy toàn bộ hệ thống
docker-compose up -d

# 3. Kiểm tra status
docker-compose ps

# 4. Xem logs
docker-compose logs -f api-gateway

# 5. Dừng hệ thống
docker-compose down
```

---

### **Giai Đoạn 9: CI/CD Pipeline (Tuần 8)**

#### Task 9.1: GitHub Actions Workflow Template

```yaml
# .github/workflows/identity-service.yml
name: Identity Service CI/CD

on:
  push:
    branches: [main, develop]
    paths:
      - "identity-service/**"
  pull_request:
    branches: [main]
    paths:
      - "identity-service/**"

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: "21"
          distribution: "temurin"

      - name: Build with Maven
        run: cd identity-service && mvn clean build

      - name: Run Tests
        run: cd identity-service && mvn test

      - name: SonarQube Analysis
        uses: sonarsource/sonarcloud-github-action@master
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

      - name: Build Docker Image
        run: |
          docker build -t ghcr.io/${{ github.repository }}/identity-service:${{ github.sha }} ./identity-service
          docker push ghcr.io/${{ github.repository }}/identity-service:${{ github.sha }}
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

      - name: Deploy to K8s (if production)
        if: github.ref == 'refs/heads/main'
        run: |
          kubectl set image deployment/identity-service \
            identity-service=ghcr.io/${{ github.repository }}/identity-service:${{ github.sha }}
```

#### Task 9.2: Testing Requirements

- Unit test coverage: >= 80%
- Integration test: Tất cả API endpoints
- Load test: 1000 concurrent users

---

### **Giai Đoạn 10: Documentation & Handover (Tuần 9)**

#### Task 10.1: API Documentation

- Sử dụng Springdoc OpenAPI (Swagger 3)
- Endpoint documentation: Mô tả, request/response examples
- URL: http://localhost:8080/swagger-ui.html

#### Task 10.2: Architecture Documentation

- Diagram: C4 model
- ADR (Architecture Decision Record)
- Data flow

#### Task 10.3: Deployment Guide

- Prerequisites: Docker, Docker Compose, Java 21
- Step-by-step: Cài đặt, cấu hình, khởi chạy
- Troubleshooting

#### Task 10.4: Developer Guide

- Project setup
- Common commands (build, test, run)
- Contributing guidelines
- Code standards

---

## 🚀 V. Kafka Topic Definitions

| Topic                 | Partitions | Replication | Producer             | Consumer                           | Message Schema                                             |
| --------------------- | ---------- | ----------- | -------------------- | ---------------------------------- | ---------------------------------------------------------- |
| `enrollment-events`   | 3          | 2           | Enrollment Service   | Notification Service, Analytics    | `{enrollmentId, userId, courseId, eventType, timestamp}`   |
| `course-events`       | 3          | 2           | Course Service       | Notification Service, Search Index | `{courseId, instructorId, eventType, timestamp}`           |
| `notification-events` | 2          | 2           | Notification Service | Archive Service                    | `{notificationId, userId, type, status, timestamp}`        |
| `payment-events`      | 2          | 2           | Enrollment Service   | Accounting Service                 | `{paymentId, userId, courseId, amount, status, timestamp}` |

---

## 📊 VI. Database Schema Summary

### PostgreSQL (Identity Service)

- Database: `identity_db`
- Tables: users, user_roles, user_profiles
- Indexes: email (unique), user_id

### PostgreSQL (Enrollment Service)

- Database: `enrollment_db`
- Tables: enrollments, lesson_progress, payments
- Indexes: user_id, course_id

### PostgreSQL (Notification Service)

- Database: `notification_db`
- Tables: notifications, email_logs
- Indexes: user_id, created_at

### MongoDB (Course Service)

- Database: `course_db`
- Collections: courses, lessons, course_ratings
- Indexes: instructor_id, category, is_published

### Redis (All Services)

- Cache keys: featured*courses, user_sessions, rate_limit*{user_id}
- TTL: 1 hour (featured courses), 24 hours (sessions)

---

## 🔐 VII. Security Checklist

- [ ] JWT token validation tại API Gateway
- [ ] HTTPS enabled (production)
- [ ] CORS properly configured
- [ ] SQL Injection protection: Sử dụng prepared statements
- [ ] Password hashing: BCrypt with salt rounds >= 12
- [ ] Rate limiting: Enable
- [ ] Environment variables: Bảo mật credentials (không commit)
- [ ] Database encryption: Enable (production)
- [ ] API key management: Sử dụng secrets manager
- [ ] Audit logging: Log all sensitive operations
- [ ] GDPR compliance: User data deletion capability

---

## 📈 VIII. Performance Targets

| Metric                  | Target         | Tool                    |
| ----------------------- | -------------- | ----------------------- |
| API Response Time (P95) | < 500ms        | Prometheus + Grafana    |
| Database Query Time     | < 100ms        | Database slow query log |
| Cache Hit Ratio         | > 80%          | Redis monitoring        |
| Service Uptime          | > 99.9%        | Health checks           |
| Throughput              | > 1000 req/sec | Load testing            |

---

## 🐛 IX. Deployment Checklist

### Pre-Deployment

- [ ] All tests passing (unit + integration)
- [ ] Code review approved
- [ ] Security scan passed (OWASP)
- [ ] Database migrations tested
- [ ] Environment variables configured
- [ ] Secrets (API keys, passwords) secured
- [ ] Backup strategy in place

### Post-Deployment

- [ ] Health checks passing
- [ ] Logs monitored for errors
- [ ] Metrics collected and visualized
- [ ] Smoke tests executed
- [ ] Alerts configured
- [ ] Rollback plan ready

---

## 📞 X. Support & Maintenance

### Monitoring Stack

- **Metrics**: Prometheus
- **Visualization**: Grafana
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **Tracing**: Zipkin
- **Alerting**: Prometheus AlertManager

### On-Call Runbooks

- Service Down Recovery
- Database Connection Issues
- Kafka Consumer Lag
- Out of Memory Issues
- High Latency Diagnosis

---

## 🎯 XI. Success Criteria

- ✅ Tất cả 5 services chạy độc lập thành công
- ✅ API Gateway định tuyến requests chính xác
- ✅ Kafka pubsub hoạt động, Notification được gửi
- ✅ JWT authentication & RBAC hoạt động
- ✅ Docker Compose khởi chạy toàn bộ stack với 1 command
- ✅ CI/CD pipeline tự động test & deploy
- ✅ Distributed tracing hiển thị request flow qua các services
- ✅ Caching hoạt động, hiệu năng cải thiện
- ✅ Load test: 1000+ concurrent users without degradation
- ✅ Documentation hoàn chỉnh & dễ hiểu

---

## 📝 XII. Notes for AI Agent

### Execution Order

1. **Không thể song song**: Database setup phải trước khi viết code
2. **Không thể bỏ qua**: Eureka Server phải chạy trước các service đăng ký
3. **Có thể song song**: Course Service & Identity Service (không phụ thuộc nhau)
4. **Phụ thuộc chặt chẽ**: Enrollment Service phụ thuộc vào Course Service (Feign client)

### Common Pitfalls to Avoid

- ❌ Không hard-code URLs: Sử dụng service discovery
- ❌ Không quên database migrations: Sử dụng Flyway hoặc Liquibase
- ❌ Không bỏ qua error handling: Circuit breaker, fallback
- ❌ Không forget to publish events: Async communication rất quan trọng
- ❌ Không test chỉ happy path: Kiểm tra error scenarios

### Verification Steps

Sau mỗi giai đoạn, AI agent phải:

```bash
# 1. Service đã register với Eureka?
curl http://localhost:8761/eureka/apps

# 2. Database đã tạo tables?
psql -U postgres -d identity_db -c "\dt"

# 3. API endpoint có respond?
curl http://localhost:8080/api/v1/courses

# 4. Logs không có errors?
docker-compose logs service-name | grep ERROR

# 5. Health check pass?
curl http://localhost:8081/actuator/health
```

---

## 📚 Tài Liệu Tham Khảo

- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Spring Boot 3 Guide](https://spring.io/projects/spring-boot)
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [MongoDB Best Practices](https://docs.mongodb.com/manual/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Docker Documentation](https://docs.docker.com/)

---

**Phiên bản**: 1.0  
**Cập nhật lần cuối**: 2026-04-30  
**Người lập**: AI Agent Specification  
**Trạng thái**: Sẵn sàng triển khai
