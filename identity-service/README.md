# Identity Service

Dịch vụ quản lý người dùng và xác thực cho hệ thống EduFlow.

## 🛠 Tính Năng
- Đăng ký và Đăng nhập người dùng.
- Quản lý thông tin cá nhân.
- Xác thực dựa trên JWT (JSON Web Token).
- Phân quyền người dùng (USER, INSTRUCTOR, ADMIN).
- Quản lý Role và Permission.

## 🏗 Công Nghệ
- **Framework**: Spring Boot 3.2.x
- **Security**: Spring Security 6.x
- **Database**: PostgreSQL
- **Migration**: Flyway
- **Discovery**: Eureka Client

## ⚙️ Cấu Hình
Cần thiết lập các biến môi trường sau hoặc cấu hình trong `application.yml`:
- `POSTGRES_HOST`: Host của PostgreSQL
- `POSTGRES_USER`: Username DB
- `POSTGRES_PASSWORD`: Password DB
- `JWT_SECRET`: Khóa bí mật dùng để ký Token
- `JWT_EXPIRATION`: Thời gian hết hạn của Token (ms)

## 📡 API Endpoints

### Authentication
- `POST /api/v1/auth/register`: Đăng ký tài khoản mới.
- `POST /api/v1/auth/login`: Đăng nhập lấy Token.
- `POST /api/v1/auth/validate`: Kiểm tra tính hợp lệ của Token.

### Users
- `GET /api/v1/users/me`: Lấy thông tin người dùng hiện tại.
- `GET /api/v1/users/{id}`: Lấy thông tin người dùng theo ID (Nội bộ).
- `PUT /api/v1/users/{id}`: Cập nhật thông tin người dùng.

## 🧪 Chạy Thử Nghiệm
```bash
mvn test
```
