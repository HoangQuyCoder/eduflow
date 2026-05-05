# ======================
# STAGE 1: BUILD
# ======================
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
RUN mvn install -N -B

COPY course-service/pom.xml course-service/
RUN mvn -f course-service/pom.xml dependency:go-offline -B

COPY course-service/src course-service/src
RUN mvn -f course-service/pom.xml clean package -DskipTests -B

# ======================
# STAGE 2: RUNTIME
# ======================
FROM eclipse-temurin:21-jre-jammy

RUN useradd -m appuser
WORKDIR /app

COPY --from=builder --chown=appuser:appuser /build/course-service/target/*.jar app.jar

USER appuser
EXPOSE 8082

ENTRYPOINT ["java","-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:InitialRAMPercentage=50.0", "-jar", "app.jar"]