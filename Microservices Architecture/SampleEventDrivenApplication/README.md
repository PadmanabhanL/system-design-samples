# Sample Event-Driven Application

A Spring Boot application demonstrating event-driven architecture using Apache Kafka.

## Architecture Overview

```
┌─────────────────┐     ┌─────────────────┐     ┌──────────────────────────────┐
│  REST Client    │────▶│  OrderController │────▶│  OrderProducerService        │
│  (POST /api/    │     │  (POST endpoint) │     │  (Sends to Kafka)            │
│   orders)       │     └─────────────────┘     └──────────────┬───────────────┘
└─────────────────┘                                            │
                                                               ▼
                                                    ┌─────────────────────┐
                                                    │   Kafka Topic       │
                                                    │   (order-events)    │
                                                    └──────────┬──────────┘
                                                               │
                              ┌────────────────────────────────┼────────────────────────────────┐
                              │                                │                                │
                              ▼                                ▼                                │
               ┌──────────────────────────┐     ┌──────────────────────────────┐               │
               │ OrderTrackingConsumer    │     │ EmailNotificationConsumer    │               │
               │ (group: order-tracking)  │     │ (group: email-notification)  │               │
               │ - Maintains in-memory    │     │ - Sends email notifications  │               │
               │   list of orders         │     │   (simulated via logs)       │               │
               └──────────────────────────┘     └──────────────────────────────┘               │
```

## Components

1. **OrderController** - REST controller exposing:
   - `POST /api/orders` - Submit an order (sends event to Kafka)
   - `GET /api/orders` - Get all submitted orders from in-memory list
   - `GET /api/orders/count` - Get total order count

2. **OrderProducerService** - Kafka producer that sends order events to the `order-events` topic

3. **OrderTrackingConsumerService** - Kafka consumer that:
   - Listens to `order-events` topic (consumer group: `order-tracking-group`)
   - Maintains an in-memory list of all submitted orders

4. **EmailNotificationConsumerService** - Kafka consumer that:
   - Listens to `order-events` topic (consumer group: `email-notification-group`)
   - Simulates sending email notifications (logs the email content)

## Prerequisites

- Java 17+
- Maven
- Docker & Docker Compose (for running Kafka)

## Running the Application

### 1. Start Kafka using Docker Compose

```bash
docker-compose up -d
```

This will start:
- Zookeeper on port 2181
- Kafka broker on port 9092

### 2. Build and Run the Spring Boot Application

```bash
./mvnw spring-boot:run
```

Or on Windows:
```bash
mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:8080`

## Testing the Application

### Submit an Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD001"}'
```

Expected Response:
```json
{
  "status": "success",
  "message": "Order submitted successfully",
  "orderId": "ORD001",
  "eventStatus": "SUBMITTED"
}
```

### Get All Submitted Orders

```bash
curl http://localhost:8080/api/orders
```

### Get Order Count

```bash
curl http://localhost:8080/api/orders/count
```

## What Happens When You Submit an Order

1. The REST controller receives the POST request with `{"orderId":"ORD001"}`
2. `OrderProducerService` creates an `OrderEvent` and sends it to the `order-events` Kafka topic
3. Two consumers receive the event (each in their own consumer group):
   - **OrderTrackingConsumerService**: Adds the order to its in-memory list
   - **EmailNotificationConsumerService**: Logs a simulated email notification

## Checking the Logs

When you submit an order, you'll see logs like:

```
INFO  OrderController - Received order submission request: OrderRequest{orderId='ORD001'}
INFO  OrderProducerService - Sending order event to Kafka: OrderEvent{orderId='ORD001', status='SUBMITTED', timestamp=...}
INFO  OrderProducerService - Order event sent successfully: ORD001 with offset: 0
INFO  OrderTrackingConsumerService - Order Tracking Service received order event: OrderEvent{...}
INFO  OrderTrackingConsumerService - Order ORD001 has been added to submitted orders list. Total orders: 1
INFO  EmailNotificationConsumerService - Email Notification Service received order event: OrderEvent{...}
INFO  EmailNotificationConsumerService - ========================================
INFO  EmailNotificationConsumerService - SENDING EMAIL NOTIFICATION
INFO  EmailNotificationConsumerService - ========================================
INFO  EmailNotificationConsumerService - To: customer@example.com
INFO  EmailNotificationConsumerService - Subject: Order ORD001 - Status Update
INFO  EmailNotificationConsumerService - Body: Your order ORD001 has been SUBMITTED.
INFO  EmailNotificationConsumerService - ========================================
```

## Stopping the Application

### Stop Kafka

```bash
docker-compose down
```

## Configuration

Key configuration in `application.properties`:

```properties
spring.kafka.bootstrap-servers=localhost:9092
app.kafka.topic.order-events=order-events