# Enrollment Service

A microservice responsible for managing course enrollments and payment processing within the EduFlow platform.

## 🛠 Features

* Enroll users in courses.
* Check enrollment status and enrollment records.
* Process orders and payments (simulated payment workflow).
* Publish successful enrollment events to Kafka for downstream services such as notifications and analytics.

## 🏗 Technology Stack

* **Database**: PostgreSQL
* **Messaging**: Kafka Producer
* **Inter-Service Communication**: OpenFeign (used to verify course availability through the Course Service)
* **Resilience**: Resilience4j Circuit Breaker

## 📡 API Endpoints

### Enrollments

* `POST /api/v1/enrollments` — Create a new course enrollment.
* `GET /api/v1/enrollments/user/{userId}` — Retrieve all courses enrolled by a specific user.
* `GET /api/v1/enrollments/course/{courseId}` — Retrieve enrollment information for a specific course.

### Payments

* `POST /api/v1/payments` — Process a payment for an enrollment order.

## ⚙️ Configuration

* **POSTGRES_HOST**: PostgreSQL database host.
* **KAFKA_BOOTSTRAP_SERVERS**: Kafka broker addresses.
* **FEIGN_CLIENT_COURSE_URL**: URL of the Course Service.

## 📖 Kafka Events

The service publishes events to the `enrollment-events` topic whenever an enrollment is successfully completed.

Example payload:

```json
{
  "enrollmentId": "uuid",
  "userId": "uuid",
  "courseId": "uuid",
  "status": "COMPLETED"
}
```

### Supported Status Values

* `PENDING`
* `PROCESSING`
* `COMPLETED`
* `FAILED`
* `CANCELLED`

## 🔍 Health Check

Endpoint:

```text
http://localhost:8084/actuator/health
```

This endpoint can be used to monitor the service's health and availability.
