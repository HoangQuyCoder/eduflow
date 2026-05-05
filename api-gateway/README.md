# API Gateway

Cổng vào duy nhất (Single Entry Point) cho hệ thống EduFlow, xử lý điều phối yêu cầu và bảo mật lớp ngoài.

## 🛠 Tính Năng
- **Routing**: Định tuyến yêu cầu đến các microservices tương ứng dựa trên Path.
- **Security**: Tích hợp xác thực JWT tập trung.
- **Service Discovery**: Tự động tìm kiếm các dịch vụ qua Eureka.
- **Load Balancing**: Cân bằng tải phía client qua Spring Cloud LoadBalancer.
- **Resilience**: Hỗ trợ Circuit Breaker (Resilience4j).

## 🏗 Công Nghệ
- **Framework**: Spring Cloud Gateway
- **Discovery**: Eureka Client
- **Security**: Spring Security (Reactive)
- **Tracing**: Micrometer Tracing

## 📡 Định Tuyến (Routes)
| Service | Path Prefix | Port (Internal) |
|---------|-------------|-----------------|
| Identity | `/api/v1/auth/**`, `/api/v1/users/**` | 8081 |
| Course | `/api/v1/courses/**`, `/api/v1/lessons/**` | 8082 |
| Enrollment | `/api/v1/enrollments/**`, `/api/v1/payments/**` | 8084 |
| Notification| `/api/v1/notifications/**` | 8085 |

## ⚙️ Cấu Hình
- `server.port`: 8080
- `eureka.client.serviceUrl.defaultZone`: URL của Eureka Server.
- `jwt.secret`: Phải trùng với Secret của Identity Service để giải mã Token.

## 🔍 Health Check
Truy cập: `http://localhost:8080/actuator/health`
