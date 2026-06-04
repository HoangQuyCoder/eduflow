# Config Server

A centralized configuration management service for the EduFlow platform, responsible for providing externalized configuration to all microservices.

## 🛠 Features

* Centralized configuration management across the entire platform.
* Uses the **Native Profile** by default to load configuration files from `classpath:/config`.
* Supports **Git Backend** configuration repositories when needed.
* Health monitoring and management endpoints via Spring Boot Actuator.
* Docker-ready deployment.
* Seamless integration with Eureka Server and API Gateway.
* Eliminates the need to rebuild services when configuration changes.

## 🏗 Technology Stack

* **Java**: 21
* **Framework**: Spring Boot 3.2.x
* **Configuration Management**: Spring Cloud Config Server
* **Monitoring**: Spring Boot Actuator
* **Build Tool**: Maven

## ⚙️ Configuration

### Core Configuration

```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  profiles:
    active: native
  cloud:
    config:
      server:
        native:
          search-locations: classpath:/config
```

### Environment Settings

| Property                                             | Description                                |
| ---------------------------------------------------- | ------------------------------------------ |
| `server.port`                                        | Config Server port (default: `8888`)       |
| `spring.profiles.active`                             | Active backend profile (`native` or `git`) |
| `spring.cloud.config.server.native.search-locations` | Location of configuration files            |

## 🚀 Running the Service

### Local Development

Start the service using Maven:

```bash
mvn spring-boot:run
```

### Docker Deployment

Build the Docker image:

```bash
docker build -t eduflow/config-server:latest .
```

Run the container:

```bash
docker run -d \
  --name config-server \
  -p 8888:8888 \
  eduflow/config-server:latest
```

### Docker Compose

For a complete platform deployment, refer to the project's `docker-compose.yml` file.

## 🌐 API Endpoints

| Method | Endpoint                   | Description                          |
| ------ | -------------------------- | ------------------------------------ |
| GET    | `/actuator/health`         | Service health status                |
| GET    | `/actuator/info`           | Application information              |
| GET    | `/{application}/{profile}` | Retrieve configuration for a service |

## 🔗 Integration with Other Services

Microservices can connect to the Config Server using:

```yaml
spring:
  config:
    import: optional:configserver:http://config-server:8888
```

During startup, each service retrieves its externalized configuration from the Config Server before initializing the application context.

## 🔍 Health Check

Endpoint:

```text
http://localhost:8888/actuator/health
```

This endpoint can be used to verify the health and availability of the Config Server.

## 📝 Notes

* The service currently uses the **Native Profile**, with configuration files stored within the project resources.
* Switching to a Git-backed configuration repository is supported by enabling the Git configuration in `application.yml`.
* The Config Server should be started before any dependent microservices.
* In production environments, using a Git repository as the configuration source is recommended for version control and centralized management.
* All EduFlow services can share configuration through this centralized service, improving maintainability and consistency.
