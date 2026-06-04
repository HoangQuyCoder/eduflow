# Course Service

A microservice responsible for managing learning content within the EduFlow platform, including courses, lessons, ratings, and reviews.

## 🛠 Features

* Course management (CRUD operations) and content publishing.
* Lesson management within individual courses.
* Ratings and reviews system for learner feedback.
* Caching featured courses to improve performance and reduce database load.
* Course search and filtering by category and difficulty level.

## 🏗 Technology Stack

* **Database**: MongoDB (Flexible document-based storage)
* **Caching**: Redis (Stores featured courses)
* **Messaging**: Kafka Producer (Publishes course-related events)
* **Inter-Service Communication**: OpenFeign (Retrieves instructor information from the Identity Service)
* **Resilience**: Resilience4j Circuit Breaker

## ⚙️ Configuration

* **MONGO_URI**: MongoDB connection string.
* **REDIS_HOST**: Redis cache host.
* **KAFKA_BOOTSTRAP_SERVERS**: Kafka broker addresses.
* **EUREKA_HOST**: Discovery Service URL.

## 📡 API Endpoints

### Courses

* `GET /api/v1/courses` — Retrieve a paginated list of courses.
* `POST /api/v1/courses` — Create a new course.
* `GET /api/v1/courses/{id}` — Retrieve course details.
* `GET /api/v1/courses/featured` — Retrieve featured courses from cache.

### Lessons

* `GET /api/v1/lessons/course/{courseId}` — Retrieve all lessons for a specific course.
* `POST /api/v1/lessons` — Create a new lesson.
* `PUT /api/v1/lessons/{id}` — Update lesson content.

### Ratings & Reviews

* `GET /api/v1/ratings/courses/{courseId}` — Retrieve ratings and reviews for a course.
* `POST /api/v1/ratings/courses/{courseId}` — Submit a new rating and review.

## 📖 Kafka Events

The service publishes events to the `course-events` topic whenever a course status changes.

Example payload:

```json
{
  "courseId": "uuid",
  "instructorId": "uuid",
  "eventType": "COURSE_PUBLISHED",
  "timestamp": 123456789
}
```

### Supported Event Types

* `COURSE_CREATED`
* `COURSE_UPDATED`
* `COURSE_PUBLISHED`
* `COURSE_UNPUBLISHED`
* `COURSE_DELETED`

## 🔍 Health Check

Endpoint:

```text
http://localhost:8082/actuator/health
```

This endpoint can be used to monitor the service's health and availability.
