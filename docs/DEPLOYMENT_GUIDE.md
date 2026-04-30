# EduFlow - Step-by-Step Implementation Guide
## Chi Tiết Các Bước Thực Thi cho AI Agent

---

## 📋 TABLE OF CONTENTS

1. [Setup Dự Án](#1-setup-dự-án)
2. [Giai Đoạn 1: Infrastructure](#2-giai-đoạn-1-infrastructure)
3. [Giai Đoạn 2: Identity Service](#3-giai-đoạn-2-identity-service)
4. [Giai Đoạn 3: Course Service](#4-giai-đoạn-3-course-service)
5. [Giai Đoạn 4: Enrollment Service](#5-giai-đoạn-4-enrollment-service)
6. [Giai Đoạn 5: Notification Service](#6-giai-đoạn-5-notification-service)
7. [Giai Đoạn 6: Integration & Testing](#7-giai-đoạn-6-integration--testing)

---

## 1. SETUP DỰ ÁN

### 1.1 Tạo GitHub Repository

```bash
# Create repository on GitHub (or use git init locally)
git clone https://github.com/YOUR_USERNAME/eduflow.git
cd eduflow

# Create folder structure
mkdir -p \
    eureka-server \
    api-gateway \
    identity-service \
    course-service \
    enrollment-service \
    notification-service \
    config-server \
    docker \
    kubernetes \
    .github/workflows \
    docs

# Create .gitignore
cat > .gitignore << 'EOF'
# Java
*.class
*.jar
*.war
*.ear
target/

# Maven
.m2/
pom.xml.tag
pom.xml.asc

# IDE
.idea/
.vscode/
*.swp
*.swo

# OS
.DS_Store
Thumbs.db

# Environment
.env
.env.local
*.properties

# Docker
docker-compose.override.yml
EOF

git add .
git commit -m "Initial project structure"
git push origin main
```

### 1.2 Tạo Root pom.xml (Multi-Module Project)

```bash
cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.eduflow</groupId>
    <artifactId>eduflow-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <name>EduFlow Parent</name>
    <description>Parent POM for EduFlow Microservices</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.0</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
    </properties>

    <modules>
        <module>eureka-server</module>
        <module>api-gateway</module>
        <module>identity-service</module>
        <module>course-service</module>
        <module>enrollment-service</module>
        <module>notification-service</module>
    </modules>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
EOF
```

### 1.3 Tạo .github/workflows/build.yml

```bash
mkdir -p .github/workflows

cat > .github/workflows/build.yml << 'EOF'
name: Build & Test All Services

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven
      
      - name: Build with Maven
        run: mvn clean package -DskipTests
      
      - name: Run Tests
        run: mvn test
      
      - name: Generate Coverage Report
        run: mvn jacoco:report
      
      - name: Upload Coverage to Codecov
        uses: codecov/codecov-action@v3
EOF
```

---

## 2. GIAI ĐOẠN 1: INFRASTRUCTURE

### 2.1 Tạo Eureka Server

```bash
cd eureka-server

# Create pom.xml
cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.eduflow</groupId>
    <artifactId>eureka-server</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.eduflow</groupId>
        <artifactId>eduflow-parent</artifactId>
        <version>1.0.0</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
EOF

# Create main application class
mkdir -p src/main/java/com/eduflow/eureka
cat > src/main/java/com/eduflow/eureka/EurekaServerApplication.java << 'EOF'
package com.eduflow.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
EOF

# Create application.yml
mkdir -p src/main/resources
cat > src/main/resources/application.yml << 'EOF'
spring:
  application:
    name: eureka-server
  profiles:
    active: default

server:
  port: 8761

eureka:
  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 5000
  client:
    register-with-eureka: false
    fetch-registry: false
  instance:
    preferIpAddress: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
EOF

# Create Dockerfile
cat > Dockerfile << 'EOF'
FROM openjdk:21-slim
WORKDIR /app
COPY target/eureka-server-1.0.0.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

cd ../
```

### 2.2 Tạo API Gateway

```bash
cd api-gateway

# Create pom.xml
cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.eduflow</groupId>
    <artifactId>api-gateway</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.eduflow</groupId>
        <artifactId>eduflow-parent</artifactId>
        <version>1.0.0</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.3</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
EOF

# Create main application class
mkdir -p src/main/java/com/eduflow/gateway
cat > src/main/java/com/eduflow/gateway/ApiGatewayApplication.java << 'EOF'
package com.eduflow.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
EOF

# Create JwtTokenProvider
cat > src/main/java/com/eduflow/gateway/security/JwtTokenProvider.java << 'EOF'
package com.eduflow.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret:mySecretKeyWith256BitsLengthForHmacSha256Encryption}")
    private String secret;
    
    @Value("${jwt.expiration:3600000}")
    private long expiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    
    public String getUserIdFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
EOF

# Create authentication filter
cat > src/main/java/com/eduflow/gateway/security/AuthenticationFilter.java << 'EOF'
package com.eduflow.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    public AuthenticationFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().writeWith(null);
            }
            
            String token = authHeader.substring(7);
            
            if (!jwtTokenProvider.validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().writeWith(null);
            }
            
            String userId = jwtTokenProvider.getUserIdFromToken(token);
            exchange.getRequest().mutate().header("X-User-Id", userId);
            
            log.info("Authenticated user: {}", userId);
            return chain.filter(exchange);
        };
    }
    
    public static class Config {}
}
EOF

# Create application.yml
mkdir -p src/main/resources
cat > src/main/resources/application.yml << 'EOF'
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: identity-service
          uri: lb://IDENTITY-SERVICE
          predicates:
            - Path=/api/v1/auth/**
            - Path=/api/v1/users/**
          filters:
            - name: AuthenticationFilter
              args:
                enabled: false
        
        - id: course-service
          uri: lb://COURSE-SERVICE
          predicates:
            - Path=/api/v1/courses/**
        
        - id: enrollment-service
          uri: lb://ENROLLMENT-SERVICE
          predicates:
            - Path=/api/v1/enrollments/**
            - Path=/api/v1/payments/**

server:
  port: 8080

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka
  instance:
    preferIpAddress: true
    leaseRenewalIntervalInSeconds: 10
    leaseExpirationDurationInSeconds: 30

management:
  endpoints:
    web:
      exposure:
        include: health,metrics
  endpoint:
    health:
      show-details: always
EOF

cd ../
```

### 2.3 Tạo Docker Compose

```bash
cd docker

cat > docker-compose.yml << 'EOF'
version: '3.8'

services:
  # PostgreSQL Databases
  postgres-identity:
    image: postgres:15-alpine
    container_name: postgres-identity
    environment:
      POSTGRES_DB: identity_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_identity_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  postgres-enrollment:
    image: postgres:15-alpine
    container_name: postgres-enrollment
    environment:
      POSTGRES_DB: enrollment_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5433:5432"
    volumes:
      - postgres_enrollment_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  postgres-notification:
    image: postgres:15-alpine
    container_name: postgres-notification
    environment:
      POSTGRES_DB: notification_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5434:5432"
    volumes:
      - postgres_notification_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  # MongoDB
  mongodb:
    image: mongo:6
    container_name: mongodb
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: password
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
    healthcheck:
      test: echo 'db.runCommand("ping").ok' | mongosh localhost:27017/test --quiet
      interval: 10s
      timeout: 5s
      retries: 5

  # Redis
  redis:
    image: redis:7-alpine
    container_name: redis
    ports:
      - "6379:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Zookeeper (for Kafka)
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  # Kafka
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  # Zipkin
  zipkin:
    image: openzipkin/zipkin:latest
    container_name: zipkin
    ports:
      - "9411:9411"

  # Eureka Server
  eureka-server:
    build:
      context: ../eureka-server
      dockerfile: Dockerfile
    container_name: eureka-server
    ports:
      - "8761:8761"
    environment:
      SPRING_PROFILES_ACTIVE: docker
    depends_on:
      - postgres-identity
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8761/eureka"]
      interval: 10s
      timeout: 5s
      retries: 5

  # API Gateway
  api-gateway:
    build:
      context: ../api-gateway
      dockerfile: Dockerfile
    container_name: api-gateway
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka
    depends_on:
      eureka-server:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  postgres_identity_data:
  postgres_enrollment_data:
  postgres_notification_data:
  mongodb_data:

networks:
  default:
    name: eduflow-network
EOF

cd ../
```

### 2.4 Xác nhận Infrastructure

```bash
# Build images
cd docker
docker-compose build

# Start infrastructure
docker-compose up -d

# Wait 30 seconds for services to start
sleep 30

# Check Eureka Server
curl http://localhost:8761/eureka/apps

# Check API Gateway health
curl http://localhost:8080/actuator/health

# View logs
docker-compose logs -f eureka-server
docker-compose logs -f api-gateway
```

---

## 3. GIAI ĐOẠN 2: IDENTITY SERVICE

### 3.1 Create Identity Service Folder & pom.xml

```bash
cd identity-service

cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.eduflow</groupId>
    <artifactId>identity-service</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.eduflow</groupId>
        <artifactId>eduflow-parent</artifactId>
        <version>1.0.0</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <dependencies>
        <!-- Spring -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.3</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
EOF
```

### 3.2 Create Directory Structure & Application Class

```bash
mkdir -p src/main/java/com/eduflow/identity/{entity,dto,repository,service,controller,security,exception,config}
mkdir -p src/main/resources/db/migration
mkdir -p src/test/java/com/eduflow/identity

# Main Application Class
cat > src/main/java/com/eduflow/identity/IdentityServiceApplication.java << 'EOF'
package com.eduflow.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableDiscoveryClient
@EnableMethodSecurity
public class IdentityServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }
}
EOF

# User Entity
cat > src/main/java/com/eduflow/identity/entity/User.java << 'EOF'
package com.eduflow.identity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Column(nullable = false)
    private String fullName;
    
    @Column(nullable = false)
    private Boolean isActive;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRole> roles = new HashSet<>();
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        isActive = true;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
EOF

# UserRole Entity
cat > src/main/java/com/eduflow/identity/entity/UserRole.java << 'EOF'
package com.eduflow.identity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_roles", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole {
    
    @Id
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID();
        createdAt = LocalDateTime.now();
    }
}

enum Role {
    ADMIN,
    TEACHER,
    STUDENT
}
EOF

# Continue creating more entities and classes...
# (Due to length limits, I'll show key classes structure)
```

### 3.3 Create Database Migration

```bash
cat > src/main/resources/db/migration/V1__Initial_Schema.sql << 'EOF'
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role)
);

CREATE TABLE user_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    avatar_url VARCHAR(500),
    bio TEXT,
    phone VARCHAR(20),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_roles ON user_roles(user_id);
EOF
```

### 3.4 Create application.yml

```bash
cat > src/main/resources/application.yml << 'EOF'
spring:
  application:
    name: identity-service
  
  datasource:
    url: jdbc:postgresql://localhost:5432/identity_db
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  
  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: 8081
  servlet:
    context-path: /

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka
  instance:
    preferIpAddress: true
    leaseRenewalIntervalInSeconds: 10
    leaseExpirationDurationInSeconds: 30

jwt:
  secret: mySecretKeyWith256BitsLengthForHmacSha256Encryption
  expiration: 3600000

management:
  endpoints:
    web:
      exposure:
        include: health,metrics
  endpoint:
    health:
      show-details: always
EOF
```

### 3.5 Create Dockerfile

```bash
cat > Dockerfile << 'EOF'
FROM maven:3.9.0-openjdk-21 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/target/identity-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

cd ../
```

---

## 4. GIAI ĐOẠN 3: COURSE SERVICE

### 4.1 Create Course Service Structure

```bash
cd course-service

cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.eduflow</groupId>
    <artifactId>course-service</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.eduflow</groupId>
        <artifactId>eduflow-parent</artifactId>
        <version>1.0.0</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
EOF

mkdir -p src/main/java/com/eduflow/course/{document,dto,repository,service,controller,config}
mkdir -p src/test/java/com/eduflow/course

# Main Application Class
cat > src/main/java/com/eduflow/course/CourseServiceApplication.java << 'EOF'
package com.eduflow.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
public class CourseServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);
    }
}
EOF

# Create application.yml
mkdir -p src/main/resources
cat > src/main/resources/application.yml << 'EOF'
spring:
  application:
    name: course-service
  
  data:
    mongodb:
      uri: mongodb://root:password@localhost:27017/course_db?authSource=admin
  
  redis:
    host: localhost
    port: 6379
    timeout: 60s
    jedis:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0

server:
  port: 8082

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka
  instance:
    preferIpAddress: true

management:
  endpoints:
    web:
      exposure:
        include: health,metrics
EOF

cat > Dockerfile << 'EOF'
FROM maven:3.9.0-openjdk-21 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/target/course-service-1.0.0.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

cd ../
```

---

## 5. GIAI ĐOẠN 4: ENROLLMENT SERVICE

### 5.1 Create Enrollment Service (Similar Structure)

```bash
cd enrollment-service

# Create pom.xml, directories, main application class
# Add Kafka, Feign, Resilience4j dependencies
# Create database schema
# Implement Feign client to Course Service
# Implement Kafka producer for enrollment events

cd ../
```

---

## 6. GIAI ĐOẠN 5: NOTIFICATION SERVICE

### 6.1 Create Notification Service

```bash
cd notification-service

# Similar structure but:
# - Include Kafka consumer instead of producer
# - Include JavaMailSender for email
# - No REST API endpoints needed
# - Listen to "enrollment-events" topic

cd ../
```

---

## 7. GIAI ĐOẠN 6: INTEGRATION & TESTING

### 7.1 Build All Services

```bash
# From root directory
mvn clean package

# Build Docker images
docker-compose -f docker/docker-compose.yml build

# Start all services
docker-compose -f docker/docker-compose.yml up -d

# Check status
docker-compose -f docker/docker-compose.yml ps
```

### 7.2 Verify Services

```bash
# 1. Check Eureka Dashboard
curl http://localhost:8761/eureka/apps

# 2. Test Identity Service
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "fullName": "John Doe"
  }'

# 3. Test Course Service
curl http://localhost:8080/api/v1/courses

# 4. Check Zipkin traces
open http://localhost:9411
```

### 7.3 Run Tests

```bash
# Unit tests
mvn test

# Integration tests
mvn verify -DskipUnitTests=true
```

---

## ✅ VERIFICATION CHECKLIST

After completing each phase, verify:

- [ ] Service is registered in Eureka
- [ ] Service health check passes
- [ ] Database migrations are applied
- [ ] API endpoints respond correctly
- [ ] Logs show no errors
- [ ] Tests pass (80%+ coverage)
- [ ] Docker image builds successfully
- [ ] Service starts in Docker container

---

## 🐛 TROUBLESHOOTING

### Service Not Registering with Eureka
```bash
# Check Eureka logs
docker-compose logs eureka-server

# Verify eureka client dependency is present
grep eureka-client pom.xml

# Check application.yml has eureka configuration
grep -A 5 "eureka:" src/main/resources/application.yml
```

### Database Connection Issues
```bash
# Test PostgreSQL connection
psql -U postgres -h localhost -d identity_db

# Check MongoDB connection
mongosh mongodb://root:password@localhost:27017

# Verify Redis
redis-cli ping
```

### Kafka Consumer Not Receiving Messages
```bash
# Check Kafka logs
docker-compose logs kafka

# List topics
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Check consumer group lag
docker exec kafka kafka-consumer-groups --bootstrap-server localhost:9092 --group notification-service --describe
```

---

**End of Implementation Guide**
