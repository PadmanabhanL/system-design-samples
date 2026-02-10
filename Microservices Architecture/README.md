# Microservices Architecture - Event-Driven Application

A demonstration of microservices architecture using Apache Kafka for event-driven communication.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              MICROSERVICES ARCHITECTURE                                  │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────┐     ┌─────────────────────────────────────────────────────────────────┐
│  REST Client    │     │              SampleEventDrivenApplication (Port 8080)           │
│                 │────▶│  ┌─────────────────┐    ┌─────────────────────────────────────┐ │
│  POST /api/     │     │  │ OrderController │───▶│ OrderProducerService                │ │
│  orders         │     │  │ (REST Endpoint) │    │ (Kafka Producer)                    │ │
└─────────────────┘     │  └─────────────────┘    └──────────────┬──────────────────────┘ │
                        └────────────────────────────────────────┼────────────────────────┘
                                                                 │
                                                                 ▼
                                                    ┌─────────────────────┐
                                                    │   Apache Kafka      │
                                                    │   Topic:            │
                                                    │   order-events      │
                                                    └──────────┬──────────┘
                                                               │
                              ┌────────────────────────────────┴────────────────────────────┐
                              │                                                             │
                              ▼                                                             ▼
┌─────────────────────────────────────────────────────┐   ┌─────────────────────────────────────────────────────┐
│        SampleKafkaConsumer (Port 8081)              │   │        NotificationService (Port 8082)              │
│  ┌───────────────────────────────────────────────┐  │   │  ┌───────────────────────────────────────────────┐  │
│  │ OrderTrackingService                          │  │   │  │ EmailNotificationService                      │  │
│  │ (Consumer Group: order-tracking-group)        │  │   │  │ (Consumer Group: email-notification-group)    │  │
│  │                                               │  │   │  │                                               │  │
│  │ - Listens to order-events topic               │  │   │  │ - Listens to order-events topic               │  │
│  │ - Maintains in-memory list of orders          │  │   │  │ - Sends email notifications (simulated)       │  │
│  │ - Exposes REST API to view orders             │  │   │  │ - Exposes REST API to view notifications      │  │
│  └───────────────────────────────────────────────┘  │   │  └───────────────────────────────────────────────┘  │
│                                                     │   │                                                     │
│  REST Endpoints:                                    │   │  REST Endpoints:                                    │
│  - GET /api/orders       (list all orders)          │   │  - GET /api/notifications       (list all)          │
│  - GET /api/orders/count (order count)              │   │  - GET /api/notifications/count (count)             │
│  - GET /api/orders/{id}  (get by ID)                │   │  - GET /api/notifications/health (health check)     │
└─────────────────────────────────────────────────────┘   └─────────────────────────────────────────────────────┘
```

## Services

### 1. SampleEventDrivenApplication (Producer Service)
- **Port:** 8080
- **Role:** REST API gateway and Kafka producer
- **Endpoints:**
  - `POST /api/orders` - Submit an order (sends event to Kafka)
  - `GET /api/orders/health` - Health check

### 2. SampleKafkaConsumer (Order Tracking Service)
- **Port:** 8081
- **Role:** Kafka consumer that tracks submitted orders
- **Consumer Group:** `order-tracking-group`
- **Endpoints:**
  - `GET /api/orders` - Get all submitted orders
  - `GET /api/orders/count` - Get total order count
  - `GET /api/orders/{orderId}` - Get order by ID

### 3. NotificationService (Email Notification Service)
- **Port:** 8082
- **Role:** Kafka consumer that sends email notifications
- **Consumer Group:** `email-notification-group`
- **Endpoints:**
  - `GET /api/notifications` - Get all sent notifications
  - `GET /api/notifications/count` - Get notification count
  - `GET /api/notifications/health` - Health check

## Prerequisites

- Java 17+
- Maven
- Docker & Docker Compose (for running Kafka and all services)

## Running the Application

### Option 1: Run Everything with Docker Compose (Recommended)

Run all services (Kafka + all microservices) with a single command:

```bash
docker-compose up --build
```

This will start:
- **Kafka** (KRaft mode - no Zookeeper needed) on port 9092
- **Order Producer Service** on port 8080
- **Order Tracking Service** on port 8081
- **Notification Service** on port 8082

To run in detached mode:
```bash
docker-compose up --build -d
```

To view logs:
```bash
docker-compose logs -f
```

To stop all services:
```bash
docker-compose down
```

### Option 2: Run Services Manually

#### 2.1 Start Kafka Only

```bash
docker-compose up kafka -d
```

This will start Kafka (KRaft mode) on port 9092.

#### 2.2 Start All Three Services Manually

Open three terminal windows and run:

**Terminal 1 - Producer Service (Port 8080):**
```bash
cd SampleEventDrivenApplication
mvn spring-boot:run
```

**Terminal 2 - Order Tracking Consumer (Port 8081):**
```bash
cd SampleKafkaConsumer
mvn spring-boot:run
```

**Terminal 3 - Notification Service (Port 8082):**
```bash
cd NotificationService
mvn spring-boot:run
```

## Testing the Application

### 1. Submit an Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD001"}'
```

Expected Response:
```json
{
  "status": "success",
  "message": "Order submitted successfully and event sent to Kafka",
  "orderId": "ORD001",
  "eventStatus": "SUBMITTED",
  "info": "Check SampleKafkaConsumer (port 8081) for order tracking and NotificationService (port 8082) for email notifications"
}
```

### 2. Check Order Tracking Service

```bash
# Get all orders
curl http://localhost:8081/api/orders

# Get order count
curl http://localhost:8081/api/orders/count

# Get specific order
curl http://localhost:8081/api/orders/ORD001
```

### 3. Check Notification Service

```bash
# Get all notifications
curl http://localhost:8082/api/notifications

# Get notification count
curl http://localhost:8082/api/notifications/count

# Health check
curl http://localhost:8082/api/notifications/health
```

## What Happens When You Submit an Order

1. **REST Request** → `SampleEventDrivenApplication` receives POST request with `{"orderId":"ORD001"}`
2. **Kafka Producer** → `OrderProducerService` creates an `OrderEvent` and sends it to the `order-events` Kafka topic
3. **Kafka Consumers** → Two independent consumers receive the event:
   - **SampleKafkaConsumer** (order-tracking-group): Adds the order to its in-memory list
   - **NotificationService** (email-notification-group): Logs a simulated email notification

## Key Concepts Demonstrated

1. **Event-Driven Architecture**: Services communicate through events via Kafka
2. **Consumer Groups**: Each service has its own consumer group, ensuring each receives all messages
3. **Loose Coupling**: Services are independent and can be scaled/deployed separately
4. **Asynchronous Communication**: Producer doesn't wait for consumers to process

## Stopping the Application

### Stop Kafka

```bash
cd SampleEventDrivenApplication
docker-compose down
```

## Project Structure

```
Microservices Architecture/
├── SampleEventDrivenApplication/     # Producer Service (Port 8080)
│   ├── src/main/java/.../
│   │   ├── controller/OrderController.java
│   │   ├── service/OrderProducerService.java
│   │   ├── config/KafkaConfig.java
│   │   ├── model/OrderEvent.java
│   │   └── dto/OrderRequest.java
│   ├── docker-compose.yml
│   └── pom.xml
│
├── SampleKafkaConsumer/              # Order Tracking Consumer (Port 8081)
│   ├── src/main/java/.../
│   │   ├── controller/OrderController.java
│   │   ├── service/OrderTrackingService.java
│   │   ├── config/KafkaConsumerConfig.java
│   │   └── model/OrderEvent.java
│   └── pom.xml
│
└── NotificationService/              # Email Notification Consumer (Port 8082)
    ├── src/main/java/.../
    │   ├── controller/NotificationController.java
    │   ├── service/EmailNotificationService.java
    │   ├── config/KafkaConsumerConfig.java
    │   └── model/OrderEvent.java
    └── pom.xml