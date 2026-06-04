# API Gateway

The single entry point for the EduFlow platform, responsible for request routing, external security enforcement, and traffic management across microservices.

## 🛠 Features

* **Routing**: Routes incoming requests to the appropriate microservices based on URL paths.
* **Security**: Provides centralized JWT authentication and authorization.
* **Service Discovery**: Automatically discovers available services through Eureka.
* **Load Balancing**: Supports client-side load balancing using Spring Cloud LoadBalancer.
* **Resilience**: Integrates Circuit Breaker functionality with Resilience4j to improve fault tolerance.

## 🏗 Technology Stack

* **Framework**: Spring Cloud Gateway
* **Service Discovery**: Eureka Client
* **Security**: Spring Security (Reactive)
* **Distributed Tracing**: Micrometer Tracing

## 📡 Route Configuration

| Service              | Path Prefix                                     | Internal Port |
| -------------------- | ----------------------------------------------- | ------------- |
| Identity Service     | `/api/v1/auth/**`, `/api/v1/users/**`           | 8081          |
| Course Service       | `/api/v1/courses/**`, `/api/v1/lessons/**`      | 8082          |
| Enrollment Service   | `/api/v1/enrollments/**`, `/api/v1/payments/**` | 8084          |
| Notification Service | `/api/v1/notifications/**`                      | 8085          |

## ⚙️ Configuration

* **server.port**: `8080`
* **eureka.client.serviceUrl.defaultZone**: URL of the Eureka Server.
* **jwt.secret**: Must match the secret key configured in the Identity Service for JWT token validation.

## 🔍 Health Check

Endpoint:

```text
http://localhost:8080/actuator/health
```

This endpoint can be used to verify the gateway's health and availability.
