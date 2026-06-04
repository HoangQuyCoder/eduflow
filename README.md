# EduFlow - Online Learning Microservices System

EduFlow is a modern online learning platform built on a **microservices architecture**, using Spring Boot, Kafka, Redis, PostgreSQL, and MongoDB.

## 🏗 System Architecture

The project consists of the following services:

1. **Eureka Server**: Service Discovery for managing microservices.
2. **API Gateway**: Single entry point for the system, handling routing and security.
3. **Identity Service**: User management, JWT authentication, and authorization.
4. **Course Service**: Manages courses, lessons, and reviews (uses MongoDB & Redis).
5. **Enrollment Service**: Handles course enrollment and payments (uses PostgreSQL).
6. **Notification Service**: Manages asynchronous notifications via Kafka (uses MongoDB).

## 🛠 Technologies Used

- **Backend**: Java 21, Spring Boot 3.2.x
- **Service Discovery**: Spring Cloud Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Databases**: 
  - PostgreSQL (Identity, Enrollment)
  - MongoDB (Course, Notification)
- **Caching**: Redis
- **Messaging**: Apache Kafka
- **Resilience**: Resilience4j (Circuit Breaker, Retry)
- **Containerization**: Docker, Docker Compose
- **CI/CD**: GitHub Actions
- **Orchestration**: Kubernetes (K8s)

## 📂 Project Structure

```text
edu-flow/
├── api-gateway/          # Single API entry point
├── course-service/       # Course and learning content management
├── enrollment-service/   # Enrollment & payment management
├── eureka-server/        # Service Discovery
├── identity-service/     # User management & Authentication
├── notification-service/ # Notification service
├── docker/               # Docker configurations & .env files
├── k8s/                  # Kubernetes manifests
└── docs/                 # API documentation & system design
```

## 🚀 Getting Started

### 1. System Requirements
- Java 21 LTS
- Maven 3.8+
- Docker & Docker Compose

### 2. Running with Docker Compose (Recommended)

```bash
# Navigate to the docker directory
cd docker

# Start the entire stack (Infrastructure + Microservices)
docker-compose up --build
```

### 3. Manual Run (Development)

If you want to run each service individually, make sure the infrastructure services (PostgreSQL, MongoDB, Kafka, Redis) are running first.

**Recommended startup order:**
1. `eureka-server` (Port 8761)
2. `identity-service` (Port 8081)
3. Other services (`course-service`, `enrollment-service`, `notification-service`)
4. `api-gateway` (Port 8080)

## 🔐 Security

The system uses **JWT (JSON Web Tokens)** for authentication.

- All requests to the API Gateway are validated for a valid token.
- The Gateway forwards `X-User-Id` and `X-User-Role` headers to downstream services.

## 📈 Monitoring

All services include **Spring Boot Actuator** and **Prometheus** metrics:

- Health check: `/actuator/health`
- Metrics endpoint: `/actuator/prometheus`

## 📄 License

This project is released under the **MIT License**.
