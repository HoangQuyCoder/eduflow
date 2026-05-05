# Eureka Server

Dịch vụ đăng ký và phát hiện dịch vụ (Service Discovery) cho hệ thống EduFlow.

## 🛠 Tính Năng
- Quản lý danh sách các instance của microservices đang hoạt động.
- Giám sát trạng thái (Health monitoring) của các dịch vụ.
- Cung cấp thông tin cho API Gateway và các dịch vụ khác để thực hiện Load Balancing.

## ⚙️ Cấu Hình
- **Cổng mặc định**: 8761
- **Dashboard**: Truy cập `http://localhost:8761` để xem giao diện quản lý.

## 🚀 Cách Chạy
```bash
mvn spring-boot:run
```

## 📝 Lưu ý
Trong môi trường Docker, các dịch vụ khác cần chờ Eureka Server sẵn sàng trước khi đăng ký.
