# ======================
# STAGE 1: BUILD
# ======================
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
RUN mvn install -N -B

COPY eureka-server/pom.xml eureka-server/
RUN mvn -f eureka-server/pom.xml dependency:go-offline -B

COPY eureka-server/src eureka-server/src
RUN mvn -f eureka-server/pom.xml clean package -DskipTests -B

# ======================
# STAGE 2: RUNTIME
# ======================
FROM eclipse-temurin:21-jre-jammy

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN useradd -m appuser
WORKDIR /app

COPY --from=builder --chown=appuser:appuser /build/eureka-server/target/*.jar app.jar

USER appuser
EXPOSE 8761

ENTRYPOINT ["java","-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:InitialRAMPercentage=50.0", "-jar", "app.jar"]