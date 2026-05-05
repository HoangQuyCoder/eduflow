# EduFlow - Hệ Thống Microservices Học Trực Tuyến

EduFlow là một nền tảng học trực tuyến được xây dựng trên kiến trúc Microservices hiện đại, sử dụng Spring Boot, Kafka, Redis, PostgreSQL và MongoDB.

## 🏗 Kiến Trúc Hệ Thống

Dự án bao gồm các dịch vụ sau:

1.  **Eureka Server**: Service Discovery để quản lý các microservices.
2.  **API Gateway**: Cổng vào duy nhất của hệ thống, xử lý định tuyến và bảo mật.
3.  **Identity Service**: Quản lý người dùng, xác thực JWT và phân quyền.
4.  **Course Service**: Quản lý khóa học, bài học và đánh giá (Sử dụng MongoDB & Redis).
5.  **Enrollment Service**: Quản lý đăng ký khóa học và thanh toán (Sử dụng PostgreSQL).
6.  **Notification Service**: Xử lý thông báo bất đồng bộ qua Kafka (Sử dụng MongoDB).

## 🛠 Công Nghệ Sử Dụng

- **Backend**: Java 21, Spring Boot 3.2.x
- **Service Discovery**: Spring Cloud Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Databases**: PostgreSQL (Identity, Enrollment), MongoDB (Course, Notification)
- **Caching**: Redis
- **Messaging**: Apache Kafka
- **Resilience**: Resilience4j (Circuit Breaker, Retry)
- **Containerization**: Docker, Docker Compose
- **CI/CD**: GitHub Actions
- **Orchestration**: Kubernetes (K8s)

## 📂 Cấu Trúc Thư Mục

```text
edu-flow/
├── api-gateway/          # Cổng API duy nhất
├── course-service/       # Quản lý nội dung học tập
├── enrollment-service/   # Quản lý đăng ký & thanh toán
├── eureka-server/        # Discovery Service
├── identity-service/     # Quản lý người dùng & Auth
├── notification-service/ # Dịch vụ thông báo
├── docker/               # Cấu hình Docker & .env
├── k8s/                  # Kubernetes Manifests
└── docs/                 # Tài liệu API & Thiết kế
```

## 🚀 Hướng Dẫn Khởi Chạy

### 1. Yêu Cầu Hệ Thống
- Java 21 LTS
- Maven 3.8+
- Docker & Docker Compose

### 2. Chạy Bằng Docker Compose (Khuyên dùng)
```bash
# Di chuyển vào thư mục docker
cd docker

# Khởi chạy toàn bộ stack (Infrastructure + Microservices)
docker-compose up --build
```

### 3. Chạy Thủ Công (Development)
Nếu bạn muốn chạy từng dịch vụ, hãy đảm bảo các hạ tầng (Postgres, Mongo, Kafka, Redis) đã sẵn sàng.
Thứ tự khởi chạy khuyến nghị:
1. `eureka-server` (Cổng 8761)
2. `identity-service` (Cổng 8081)
3. Các dịch vụ khác (`course`, `enrollment`, `notification`)
4. `api-gateway` (Cổng 8080)

## 🔐 Bảo Mật
Hệ thống sử dụng **JWT (JSON Web Token)** để xác thực.
- Các yêu cầu đến Gateway sẽ được kiểm tra Token.
- Gateway chuyển tiếp `X-User-Id` và `X-User-Role` xuống các dịch vụ bên dưới.

## 📈 Monitoring
Các dịch vụ đều tích hợp **Spring Boot Actuator** và **Prometheus**:
- Health check: `/actuator/health`
- Metrics: `/actuator/prometheus`

## 📄 Giấy Phép
Dự án được phát hành dưới giấy phép MIT.
