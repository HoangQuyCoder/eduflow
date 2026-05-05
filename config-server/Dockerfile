# ======================
# STAGE 1: BUILD
# ======================
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
RUN mvn install -N -B

COPY config-server/pom.xml config-server/
RUN mvn -f config-server/pom.xml dependency:go-offline -B

COPY config-server/src config-server/src
RUN mvn -f config-server/pom.xml clean package -DskipTests -B

# ======================
# STAGE 2: RUNTIME
# ======================
FROM eclipse-temurin:21-jre-jammy

RUN useradd -m appuser
WORKDIR /app

COPY --from=builder --chown=appuser:appuser /build/config-server/target/*.jar app.jar

USER appuser
EXPOSE 8888

ENTRYPOINT ["java","-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:InitialRAMPercentage=50.0", "-jar", "app.jar"]
