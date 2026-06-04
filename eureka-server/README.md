# Eureka Server

The Service Discovery component of the EduFlow platform, responsible for service registration, discovery, and monitoring across the microservices ecosystem.

## 🛠 Features

* Maintains a registry of active microservice instances.
* Monitors service health and availability.
* Provides service location information to the API Gateway and other services.
* Enables client-side load balancing and dynamic service discovery.
* Reduces dependency on hardcoded service endpoints.

## 🏗 Technology Stack

* **Framework**: Spring Cloud Netflix Eureka Server
* **Service Discovery**: Eureka
* **Monitoring**: Built-in Eureka Dashboard

## ⚙️ Configuration

* **Default Port**: `8761`
* **Dashboard URL**:

```text
http://localhost:8761
```

The dashboard provides a real-time view of registered services and their current status.

## 🚀 Running the Service

Start the Eureka Server using:

```bash
mvn spring-boot:run
```

Once started, other microservices can register themselves with the service registry and discover each other dynamically.

## 🔍 Health Check

Endpoint:

```text
http://localhost:8761/actuator/health
```

This endpoint can be used to verify the health and availability of the Eureka Server.

## 📝 Notes

* In a Docker environment, dependent services should wait until the Eureka Server is fully initialized before attempting registration.
* All client services must be configured with the correct Eureka Server URL using the `eureka.client.serviceUrl.defaultZone` property.
* The Eureka Server itself does not contain business logic; it is dedicated to service registration and discovery.
