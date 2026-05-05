# ======================
# STAGE 1: BUILD
# ======================
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

# Copy pom files first for dependency caching
COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/

# Download dependencies (this layer will be cached)
RUN mvn -f eureka-server/pom.xml dependency:go-offline -B

# Copy source code
COPY eureka-server/src eureka-server/src

# Build
RUN mvn -f eureka-server/pom.xml clean package -DskipTests -B

# ======================
# STAGE 2: RUNTIME
# ======================
FROM eclipse-temurin:21-jre-jammy

RUN useradd -m appuser
WORKDIR /app

COPY --from=builder --chown=appuser:appuser /build/eureka-server/target/*.jar app.jar

USER appuser
EXPOSE 8761

ENTRYPOINT ["java","-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:InitialRAMPercentage=50.0", "-jar", "app.jar"]