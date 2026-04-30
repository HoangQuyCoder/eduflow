# EduFlow - Architecture Deep Dive
## Hướng dẫn Kiến trúc Chi Tiết cho AI Agent

---

## 1. Service Communication Patterns

### 1.1 Synchronous Communication (Request-Response)
**Khi nào dùng**: Cần kết quả ngay lập tức

**Ví dụ**: Enrollment Service gọi Course Service để check course có tồn tại không
```java
// Enrollment Service
@FeignClient(name = "course-service")
public interface CourseClient {
    @GetMapping("/api/v1/courses/{id}")
    CourseDTO getCourseById(@PathVariable String id);
}

// Usage
public void enrollToCourse(String courseId, String userId) {
    CourseDTO course = courseClient.getCourseById(courseId);
    
    if (course == null) {
        throw new CourseNotFoundException("Course not found");
    }
    
    Enrollment enrollment = new Enrollment(userId, courseId);
    enrollmentRepository.save(enrollment);
}
```

**Failure Handling**:
```java
@CircuitBreaker(name = "courseService", fallback = "handleCourseServiceDown")
public CourseDTO getCourse(String courseId) {
    return courseClient.getCourseById(courseId);
}

public CourseDTO handleCourseServiceDown(String courseId) {
    log.warn("Course Service is down, using fallback");
    // Return cached data hoặc default response
    return courseCache.getOrDefault(courseId, null);
}
```

### 1.2 Asynchronous Communication (Event-Driven)
**Khi nào dùng**: Không cần kết quả ngay lập tức, muốn decouple services

**Ví dụ**: Khi enrollment được tạo, publish event cho Notification Service
```java
// Enrollment Service - Producer
@Service
public class EnrollmentService {
    @Autowired
    private KafkaTemplate<String, EnrollmentEvent> kafkaTemplate;
    
    public Enrollment enrollCourse(String userId, String courseId) {
        // Create enrollment
        Enrollment enrollment = new Enrollment(userId, courseId);
        enrollment = enrollmentRepository.save(enrollment);
        
        // Publish event (Asynchronous)
        EnrollmentEvent event = EnrollmentEvent.builder()
            .enrollmentId(enrollment.getId())
            .userId(userId)
            .courseId(courseId)
            .eventType("ENROLLMENT_CREATED")
            .timestamp(LocalDateTime.now())
            .build();
            
        kafkaTemplate.send("enrollment-events", event);
        
        return enrollment;
    }
}

// Notification Service - Consumer
@Service
public class NotificationConsumer {
    @KafkaListener(
        topics = "enrollment-events",
        groupId = "notification-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleEnrollmentEvent(EnrollmentEvent event) {
        if ("ENROLLMENT_CREATED".equals(event.getEventType())) {
            log.info("Received enrollment event for user: {}", event.getUserId());
            
            // Send welcome email
            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setRecipient(getUserEmail(event.getUserId()));
            emailRequest.setSubject("Welcome to our course!");
            emailRequest.setBody(generateWelcomeEmailTemplate(event));
            
            emailService.sendEmail(emailRequest);
        }
    }
}
```

**Advantages**:
- Enrollment Service không cần đợi email gửi xong
- Nếu Notification Service sập, Enrollment vẫn hoạt động
- Có thể thêm consumers mới (e.g., Analytics Service) mà không sửa Enrollment Service

---

## 2. Data Consistency Strategy

### 2.1 Problem: Distributed Transaction
**Tình huống**: Enrollment Service & Course Service đều cần update

Scenario: Giảng viên edit khóa học lúc học viên đang enroll
```
Time 1: Course Service: Update course name
Time 2: Enrollment Service: Create enrollment
Time 3: Notification Service: Gửi email (dùng cái tên nào?)
```

### 2.2 Solution: Event Sourcing + SAGA Pattern
```
Enrollment Request
    ↓
[1] Create Enrollment in PENDING state
    ↓
Publish: "enrollment_initiated" event
    ↓
[2] Course Service listens → verify course exists
    ↓
If OK: Publish "enrollment_verified"
If NOT: Publish "enrollment_failed" → Compensation
    ↓
[3] Payment Service listens → process payment
    ↓
If OK: Publish "payment_completed" → Mark enrollment as CONFIRMED
If NOT: Publish "payment_failed" → Refund & Cancel enrollment
    ↓
[4] Notification Service listens → send confirmation email
    ↓
Final State: CONFIRMED or CANCELLED
```

### 2.3 Database per Service Pattern
```
┌─────────────────────────────────────────────────────────────┐
│                     Shared Database (AVOIDED)                │
│                     ❌ DO NOT DO THIS ❌                     │
│  All services access same DB = Tight coupling               │
│  One service down = potential data corruption               │
└─────────────────────────────────────────────────────────────┘

                           CORRECT:

┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ Identity Service │  │ Course Service   │  │Enrollment Service│
├──────────────────┤  ├──────────────────┤  ├──────────────────┤
│  PostgreSQL      │  │  MongoDB         │  │  PostgreSQL      │
│  identity_db     │  │  course_db       │  │  enrollment_db   │
│                  │  │                  │  │                  │
│ users            │  │ courses          │  │ enrollments      │
│ user_roles       │  │ lessons          │  │ lesson_progress  │
│ user_profiles    │  │ ratings          │  │ payments         │
└──────────────────┘  └──────────────────┘  └──────────────────┘
```

**Nếu cần data từ service khác**:
```java
// Option 1: Gọi API (Synchronous)
CourseDTO course = courseClient.getCourseById(courseId);

// Option 2: Subscribe to events (Asynchronous)
@KafkaListener(topics = "course-events")
public void onCourseUpdated(CourseEvent event) {
    // Cache course info locally
    courseCache.put(event.getCourseId(), event.getUpdatedCourse());
}

// Option 3: Read-model replication (CQRS)
// Enrollment Service có bản copy của course data (read-only)
// Được sync qua Kafka topic "course-events"
```

---

## 3. Security Architecture

### 3.1 JWT Token Flow
```
┌──────────┐
│  Client  │
└────┬─────┘
     │ POST /api/v1/auth/login
     │ {email: "user@example.com", password: "..."}
     │
     ▼
┌──────────────────────┐
│  API Gateway         │
│  (No JWT needed)     │
└────┬────────────────┘
     │
     ▼
┌──────────────────────────────────────────┐
│  Identity Service /api/v1/auth/login     │
│  1. Verify email & password               │
│  2. Generate JWT token                    │
│  3. Return token                          │
└────┬─────────────────────────────────────┘
     │
     │ Response: {
     │   "accessToken": "eyJhbGciOiJIUzI1NiIs...",
     │   "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
     │   "expiresIn": 3600
     │ }
     │
     ▼
┌──────────────────────────────────────────┐
│  Client stores token                     │
│  (localStorage hoặc secure cookie)       │
└──────────────────────────────────────────┘

                SUBSEQUENT REQUESTS:

┌──────────────────────────────────────────┐
│  Client                                  │
│  Authorization: Bearer {accessToken}     │
└────┬─────────────────────────────────────┘
     │
     ▼
┌──────────────────────────────────────────┐
│  API Gateway                             │
│  1. Extract token from header            │
│  2. Call Identity Service /validate      │
│  3. If valid: Add user info to request   │
│  4. Forward to service                   │
└────┬─────────────────────────────────────┘
     │
     ▼
┌──────────────────────────────────────────┐
│  Downstream Service                      │
│  (Course Service, Enrollment Service)    │
│  Receive user context from request       │
│  Can check user role/permissions         │
└──────────────────────────────────────────┘
```

### 3.2 JWT Token Structure
```
Header: {
  "alg": "HS256",
  "typ": "JWT"
}

Payload: {
  "sub": "user-id-uuid",
  "email": "user@example.com",
  "name": "John Doe",
  "roles": ["STUDENT", "TEACHER"],
  "iat": 1704067200,
  "exp": 1704153600,  // 24 hours
  "iss": "eduflow"
}

Signature: HMACSHA256(base64(header) + "." + base64(payload), SECRET_KEY)
```

### 3.3 Role-Based Access Control (RBAC)
```java
// Define roles
public enum Role {
    ADMIN,      // Toàn quyền
    TEACHER,    // Tạo & quản lý khóa học
    STUDENT     // Đăng ký & xem khóa học
}

// Usage in controller
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    
    // Anyone can view
    @GetMapping
    public ResponseEntity<Page<CourseDTO>> listCourses(...) { ... }
    
    // Only TEACHER & ADMIN can create
    @PostMapping
    @RequireRole({Role.TEACHER, Role.ADMIN})
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseRequest request) { ... }
    
    // Only course owner or ADMIN can update
    @PutMapping("/{id}")
    @RequireRole({Role.TEACHER, Role.ADMIN})
    public ResponseEntity<CourseDTO> updateCourse(
        @PathVariable String id,
        @RequestBody CourseRequest request
    ) {
        Course course = courseRepository.findById(id);
        
        // Check ownership
        if (!course.getInstructorId().equals(getCurrentUserId()) 
            && !hasRole(Role.ADMIN)) {
            throw new UnauthorizedException("You can only edit your own courses");
        }
        
        ...
    }
}
```

---

## 4. Caching Strategy

### 4.1 Cache Layers
```
┌──────────────────────────────┐
│  Application Layer Cache     │
│  @Cacheable on methods       │
└──────────────────────────────┘
           ↓
┌──────────────────────────────┐
│  Redis Cache                 │
│  Distributed, persistent     │
└──────────────────────────────┘
           ↓
┌──────────────────────────────┐
│  Database                    │
│  Source of truth             │
└──────────────────────────────┘
```

### 4.2 Cache Invalidation Patterns
```java
// Pattern 1: Time-based (TTL)
@Cacheable(value = "featured_courses", cacheManager = "redisCacheManager")
public List<CourseDTO> getFeaturedCourses() {
    return courseRepository.findFeaturedCourses();
}
// Tự động hết cache sau 1 giờ

// Pattern 2: Event-driven invalidation
@PostMapping
@CacheEvict(value = "featured_courses", allEntries = true)
public CourseDTO publishCourse(@RequestBody CourseRequest request) {
    // Khi có course mới được publish, clear toàn bộ featured cache
    return courseService.createAndPublish(request);
}

// Pattern 3: Cache aside
public CourseDTO getCourse(String courseId) {
    // Check cache first
    String cacheKey = "course:" + courseId;
    CourseDTO cached = redisTemplate.opsForValue().get(cacheKey);
    
    if (cached != null) {
        return cached;
    }
    
    // Miss → fetch from DB
    CourseDTO course = courseRepository.findById(courseId);
    
    if (course != null) {
        // Update cache
        redisTemplate.opsForValue().set(cacheKey, course, 
            Duration.ofHours(1));
    }
    
    return course;
}
```

### 4.3 What to Cache
```
✅ Featured courses (low update frequency, high access)
✅ User session (accessed on every request)
✅ Course metadata (title, description, tags)
✅ Role-based permissions (rarely changes)
❌ Real-time data (user progress percentage)
❌ Payment info (must always be current)
❌ User enrollments (changes frequently)
```

---

## 5. Error Handling & Resilience

### 5.1 Circuit Breaker Pattern
```
┌─────────────────────────────────┐
│  Normal Traffic Flow            │
│  Course Service is responding   │
│  Status: CLOSED                 │
│  Requests: PASSED through       │
└─────────────────────────────────┘
        ↓ (errors spike)
┌─────────────────────────────────┐
│  OPEN state                     │
│  Requests immediately fail      │
│  Fallback response returned     │
│  No calls to failing service    │
└─────────────────────────────────┘
        ↓ (wait timeout: 30s)
┌─────────────────────────────────┐
│  HALF_OPEN state                │
│  Allow 1 test request           │
│  If succeeds: back to CLOSED    │
│  If fails: back to OPEN         │
└─────────────────────────────────┘
```

### 5.2 Implementation
```java
@Configuration
@EnableAspectJAutoProxy
public class ResilienceConfig {
    
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)           // 50% failure triggers
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .permittedNumberOfCallsInHalfOpenState(3)
            .build();
        
        return CircuitBreakerRegistry.of(config);
    }
}

// Usage
@Service
public class EnrollmentService {
    
    @CircuitBreaker(name = "courseService")
    @Retry(name = "courseService", fallback = "handleCourseServiceDown")
    public CourseDTO getCourse(String courseId) {
        return courseClient.getCourseById(courseId);
    }
    
    public CourseDTO handleCourseServiceDown(String courseId) {
        log.error("Course Service is down for course: {}", courseId);
        // Return cached data or dummy object
        return courseCache.get(courseId);
    }
}
```

### 5.3 Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
        ResourceNotFoundException ex,
        HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("NOT_FOUND")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
        UnauthorizedException ex,
        HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("UNAUTHORIZED")
            .message("Invalid or missing authentication token")
            .path(request.getRequestURI())
            .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
        Exception ex,
        HttpServletRequest request
    ) {
        log.error("Unexpected error: ", ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("INTERNAL_SERVER_ERROR")
            .message("An unexpected error occurred")
            .path(request.getRequestURI())
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }
}
```

---

## 6. Kafka Configuration

### 6.1 Topic Setup
```bash
# Create topics
kafka-topics --create \
  --topic enrollment-events \
  --partitions 3 \
  --replication-factor 2 \
  --bootstrap-server localhost:9092

kafka-topics --create \
  --topic course-events \
  --partitions 3 \
  --replication-factor 2 \
  --bootstrap-server localhost:9092

kafka-topics --create \
  --topic notification-events \
  --partitions 2 \
  --replication-factor 2 \
  --bootstrap-server localhost:9092
```

### 6.2 Producer Configuration
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all  # Wait for all replicas acknowledgment
      retries: 3
      properties:
        linger.ms: 10  # Batch messages
        batch.size: 16384
```

### 6.3 Consumer Configuration
```yaml
spring:
  kafka:
    consumer:
      bootstrap-servers: localhost:9092
      group-id: notification-service
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.eduflow.events"
        auto.offset.reset: earliest
      max-poll-records: 100
      session-timeout-ms: 30000
```

### 6.4 Error Handling
```java
@Service
public class EnrollmentEventConsumer {
    
    @KafkaListener(
        topics = "enrollment-events",
        groupId = "notification-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleEnrollmentEvent(
        EnrollmentEvent event,
        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
        @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            processEvent(event);
        } catch (Exception e) {
            log.error("Failed to process event at partition: {}, offset: {}", 
                partition, offset, e);
            
            // Send to dead letter topic
            deadLetterTemplate.send("enrollment-events-dlq", event);
        }
    }
}
```

---

## 7. Testing Strategy

### 7.1 Unit Testing
```java
@SpringBootTest
@ActiveProfiles("test")
public class CourseServiceTest {
    
    @MockBean
    private CourseRepository courseRepository;
    
    @InjectMocks
    private CourseService courseService;
    
    @Test
    public void testGetCourseById_CourseExists() {
        // Arrange
        String courseId = "course-123";
        Course mockCourse = new Course();
        mockCourse.setId(courseId);
        mockCourse.setTitle("Java Basics");
        
        when(courseRepository.findById(courseId))
            .thenReturn(Optional.of(mockCourse));
        
        // Act
        CourseDTO result = courseService.getCourseById(courseId);
        
        // Assert
        assertNotNull(result);
        assertEquals("Java Basics", result.getTitle());
        verify(courseRepository, times(1)).findById(courseId);
    }
    
    @Test
    public void testGetCourseById_CourseNotFound() {
        // Arrange
        String courseId = "non-existent";
        when(courseRepository.findById(courseId))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(CourseNotFoundException.class, () -> {
            courseService.getCourseById(courseId);
        });
    }
}
```

### 7.2 Integration Testing
```java
@SpringBootTest
@Testcontainers
public class CourseServiceIntegrationTest {
    
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(
        DockerImageName.parse("mongo:6")
    );
    
    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(
        DockerImageName.parse("redis:7")
    ).withExposedPorts(6379);
    
    @Test
    public void testCreateCourseEndToEnd() {
        // Arrange
        CourseRequest request = new CourseRequest();
        request.setTitle("Advanced Java");
        request.setInstructorId("teacher-123");
        
        // Act
        ResponseEntity<CourseDTO> response = 
            restTemplate.postForEntity("/api/v1/courses", request, CourseDTO.class);
        
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals("Advanced Java", response.getBody().getTitle());
    }
}
```

### 7.3 Load Testing
```java
public class PerformanceTest {
    
    @Test
    public void testCourseListPerformance() {
        // Load test: 1000 concurrent users fetching course list
        
        int threadCount = 100;
        int requestsPerThread = 10;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    try {
                        ResponseEntity<Page<CourseDTO>> response = 
                            restTemplate.exchange(
                                "/api/v1/courses?page=0&size=20",
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<Page<CourseDTO>>() {}
                            );
                        
                        if (response.getStatusCode() == HttpStatus.OK) {
                            successCount.incrementAndGet();
                        } else {
                            failureCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                    }
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        int totalRequests = threadCount * requestsPerThread;
        double requestsPerSecond = (totalRequests * 1000.0) / duration;
        
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Successful: " + successCount.get());
        System.out.println("Failed: " + failureCount.get());
        System.out.println("Duration: " + duration + "ms");
        System.out.println("Throughput: " + requestsPerSecond + " req/sec");
        
        assertTrue(requestsPerSecond > 1000, "Should handle 1000+ req/sec");
    }
}
```

---

## 8. Monitoring & Observability

### 8.1 Metrics Collection (Micrometer + Prometheus)
```java
@Configuration
public class MetricsConfig {
    
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsConfig() {
        return registry -> {
            registry.counter("enrollment.created", "service", "enrollment");
            registry.counter("course.published", "service", "course");
            registry.timer("http.request.duration", "endpoint", "/api/v1/courses");
        };
    }
}

// Usage in code
@Service
public class EnrollmentService {
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    public Enrollment enrollCourse(String userId, String courseId) {
        Enrollment enrollment = enrollmentRepository.save(...);
        
        // Record metric
        meterRegistry.counter("enrollment.created", 
            "course", courseId, 
            "status", "success"
        ).increment();
        
        return enrollment;
    }
}
```

### 8.2 Distributed Tracing
```java
@Configuration
public class TracingConfig {
    
    @Bean
    public Tracer tracer(ObservationRegistry registry) {
        return Tracer.create(registry);
    }
}

// Usage
@Service
public class CourseService {
    
    @Autowired
    private Tracer tracer;
    
    public CourseDTO getCourse(String courseId) {
        Span span = tracer.nextSpan()
            .name("getCourse")
            .tag("courseId", courseId);
        
        try (Tracer.SpanInScope scope = tracer.withSpan(span)) {
            return courseRepository.findById(courseId);
        } finally {
            span.finish();
        }
    }
}
```

### 8.3 Logging Best Practices
```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EnrollmentService {
    
    public void enrollCourse(EnrollmentRequest request) {
        String correlationId = UUID.randomUUID().toString();
        
        log.info("Starting enrollment process | correlationId: {} | userId: {} | courseId: {}",
            correlationId, request.getUserId(), request.getCourseId());
        
        try {
            validateRequest(request);
            log.debug("Request validation passed | correlationId: {}", correlationId);
            
            Enrollment enrollment = enrollmentRepository.save(...);
            log.info("Enrollment created successfully | correlationId: {} | enrollmentId: {}",
                correlationId, enrollment.getId());
                
        } catch (ValidationException e) {
            log.warn("Enrollment validation failed | correlationId: {} | error: {}",
                correlationId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during enrollment | correlationId: {}",
                correlationId, e);
            throw new EnrollmentException("Failed to enroll course", e);
        }
    }
}
```

---

## 9. Deployment Architecture

### 9.1 Local Development (Docker Compose)
```
developer laptop
    ↓
docker-compose up -d
    ↓
┌─────────────────────────────────────────┐
│ Docker Network (eduflow)                │
│                                          │
│ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐  │
│ │Eureka│ │ API  │ │Course│ │Enroll│  │
│ │Server│ │Gate  │ │Srv   │ │Srv   │  │
│ └──────┘ └──────┘ └──────┘ └──────┘  │
│     │        │        │        │      │
│ ┌────────────────────────────────────┐ │
│ │        Postgres + MongoDB + Redis   │ │
│ │        Kafka + Zipkin               │ │
│ └────────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

### 9.2 Production (Kubernetes)
```
┌─────────────────────────────────────────────┐
│ Kubernetes Cluster                          │
│                                              │
│ ┌──────────────────────────────────────┐   │
│ │ Ingress / API Gateway                │   │
│ └──────┬───────────────────────────────┘   │
│        │                                    │
│ ┌──────┴──────────┬──────────┬──────────┐   │
│ │                 │          │          │   │
│ ▼                 ▼          ▼          ▼   │
│ Pod(Identity)     Pod(Course) Pod(Enroll)  │
│ Pod(Identity)     Pod(Course) Pod(Enroll)  │
│ Pod(Identity)     Pod(Course) Pod(Enroll)  │
│        │                 │          │      │
│ ┌──────┴─────────┬───────┴──────────┘      │
│ │                │                         │
│ ▼                ▼                         │
│ PostgreSQL       MongoDB                   │
│ (Stateful Set)   (Stateful Set)            │
│                                              │
│ Redis (Cache)    Kafka (Message Broker)    │
│ (Helm Chart)     (Helm Chart)              │
└─────────────────────────────────────────────┘
```

---

## 10. Architecture Decision Records (ADR)

### ADR-001: Polyglot Persistence
**Decision**: Use PostgreSQL for Identity/Enrollment, MongoDB for Course

**Rationale**:
- Identity: Relational, strict schema, ACID transactions
- Enrollment: Financial transactions, need strong consistency
- Course: Flexible schema (ratings, attachments), document structure

**Consequences**:
- ✅ Optimal DB for each use case
- ❌ Higher operational complexity
- ❌ Need to learn multiple DB query languages

---

### ADR-002: Event-Driven Notification
**Decision**: Use Kafka for async communication between services

**Rationale**:
- Decouples Enrollment from Notification
- Notification delays don't block enrollment
- Can replay events if service was down

**Consequences**:
- ✅ Better resilience
- ❌ Eventual consistency (notification might be delayed)
- ❌ Need to handle duplicate events

---

### ADR-003: API Gateway Pattern
**Decision**: Single entry point for all client requests

**Rationale**:
- Centralized authentication
- Consistent rate limiting
- Easier to add cross-cutting concerns

**Consequences**:
- ✅ Simplified client code
- ❌ Potential bottleneck (need proper load balancing)
- ❌ Gateway becomes critical component

---

**End of Architecture Documentation**
