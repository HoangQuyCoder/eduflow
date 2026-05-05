# ======================
# STAGE 1: BUILD
# ======================
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
RUN mvn install -N -B

COPY identity-service/pom.xml identity-service/
RUN mvn -f identity-service/pom.xml dependency:go-offline -B

COPY identity-service/src identity-service/src
RUN mvn -f identity-service/pom.xml clean package -DskipTests -B

# ======================
# STAGE 2: RUNTIME
# ======================
FROM eclipse-temurin:21-jre-jammy

RUN useradd -m appuser
WORKDIR /app

COPY --from=builder --chown=appuser:appuser /build/identity-service/target/*.jar app.jar

USER appuser
EXPOSE 8080

ENTRYPOINT ["java","-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:InitialRAMPercentage=50.0", "-jar", "app.jar"]