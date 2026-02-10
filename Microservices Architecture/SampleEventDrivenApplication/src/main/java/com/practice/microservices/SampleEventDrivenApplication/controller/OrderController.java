package com.practice.microservices.SampleEventDrivenApplication.controller;

import com.practice.microservices.SampleEventDrivenApplication.dto.OrderRequest;
import com.practice.microservices.SampleEventDrivenApplication.entity.Order;
import com.practice.microservices.SampleEventDrivenApplication.entity.ServiceMetrics;
import com.practice.microservices.SampleEventDrivenApplication.model.OrderEvent;
import com.practice.microservices.SampleEventDrivenApplication.service.OrderProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderProducerService orderProducerService;

    public OrderController(OrderProducerService orderProducerService) {
        this.orderProducerService = orderProducerService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitOrder(@RequestBody OrderRequest orderRequest) {
        logger.info("Received order submission request: {}", orderRequest);

        if (orderRequest.getOrderId() == null || orderRequest.getOrderId().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Order ID is required");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Create order event and send to Kafka
        OrderEvent orderEvent = new OrderEvent(orderRequest.getOrderId(), "SUBMITTED");
        orderProducerService.sendOrderEvent(orderEvent);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Order submitted successfully and event sent to Kafka");
        response.put("orderId", orderRequest.getOrderId());
        response.put("eventId", orderEvent.getEventId());
        response.put("eventStatus", "SUBMITTED");
        response.put("info", "Check SampleKafkaConsumer (port 8081) for order tracking and NotificationService (port 8082) for email notifications");

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * IDEMPOTENCY SIMULATION ENDPOINT
     * This endpoint allows you to send the same event multiple times with the same eventId
     * to demonstrate that consumers will skip duplicate events.
     * 
     * Usage:
     * 1. First call: POST /api/orders/simulate-duplicate?orderId=ORDER-123&times=3
     *    - This will send the SAME event (same eventId) 3 times to Kafka
     *    - Only the FIRST event should be processed by consumers
     *    - Subsequent events should be skipped due to idempotency check
     */
    @PostMapping("/simulate-duplicate")
    public ResponseEntity<Map<String, Object>> simulateDuplicateMessages(
            @RequestParam String orderId,
            @RequestParam(defaultValue = "3") int times) {
        
        logger.info("========================================");
        logger.info("IDEMPOTENCY SIMULATION - Sending {} duplicate messages for order: {}", times, orderId);
        logger.info("========================================");

        // Create ONE event with a fixed eventId
        String fixedEventId = UUID.randomUUID().toString();
        OrderEvent orderEvent = new OrderEvent(orderId, "SUBMITTED");
        orderEvent.setEventId(fixedEventId);  // Set the same eventId for all sends

        List<Map<String, Object>> sendResults = new ArrayList<>();

        // Send the SAME event multiple times
        for (int i = 1; i <= times; i++) {
            logger.info("Sending duplicate message #{} with eventId: {}", i, fixedEventId);
            
            // Send to Kafka (same event, same eventId)
            orderProducerService.sendOrderEventWithoutSaving(orderEvent);
            
            Map<String, Object> result = new HashMap<>();
            result.put("attempt", i);
            result.put("eventId", fixedEventId);
            result.put("orderId", orderId);
            result.put("sentAt", LocalDateTime.now().toString());
            sendResults.add(result);
            
            // Small delay between sends
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", String.format("Sent %d duplicate messages with the SAME eventId", times));
        response.put("eventId", fixedEventId);
        response.put("orderId", orderId);
        response.put("totalSent", times);
        response.put("sendResults", sendResults);
        response.put("expectedBehavior", "Only the FIRST message should be processed. Check consumer logs for 'Duplicate event detected' messages.");
        response.put("verifyAt", Map.of(
            "orderTracking", "http://localhost:8081/api/orders",
            "notifications", "http://localhost:8082/api/notifications"
        ));

        return ResponseEntity.ok(response);
    }

    /**
     * Send event with a specific eventId (for manual testing)
     * This allows you to manually control the eventId to test idempotency
     */
    @PostMapping("/with-event-id")
    public ResponseEntity<Map<String, Object>> submitOrderWithEventId(
            @RequestParam String orderId,
            @RequestParam String eventId,
            @RequestParam(defaultValue = "SUBMITTED") String status) {
        
        logger.info("Sending order with specific eventId: {} for orderId: {}", eventId, orderId);

        OrderEvent orderEvent = new OrderEvent(orderId, status);
        orderEvent.setEventId(eventId);  // Use the provided eventId
        
        orderProducerService.sendOrderEventWithoutSaving(orderEvent);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Order event sent with specified eventId");
        response.put("eventId", eventId);
        response.put("orderId", orderId);
        response.put("orderStatus", status);
        response.put("note", "If this eventId was already processed, consumers will skip this message");

        return ResponseEntity.ok(response);
    }

    /**
     * Get all orders from the producer's database
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        logger.info("Fetching all orders from producer database");
        return ResponseEntity.ok(orderProducerService.getAllOrders());
    }

    /**
     * Get order count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getOrderCount() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalOrders", orderProducerService.getOrderCount());
        response.put("service", "Order Producer Service");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "SampleEventDrivenApplication - Order Producer Service");
        response.put("port", 8080);
        response.put("idempotencyEndpoints", Map.of(
            "simulateDuplicate", "POST /api/orders/simulate-duplicate?orderId=ORDER-123&times=3",
            "withEventId", "POST /api/orders/with-event-id?orderId=ORDER-123&eventId=my-event-id"
        ));
        return ResponseEntity.ok(response);
    }

    // ==================== METRICS ENDPOINTS ====================

    @GetMapping("/metrics")
    public ResponseEntity<List<ServiceMetrics>> getAllMetrics() {
        logger.info("Fetching all service metrics");
        return ResponseEntity.ok(orderProducerService.getAllMetrics());
    }

    @GetMapping("/metrics/recent")
    public ResponseEntity<List<ServiceMetrics>> getRecentMetrics() {
        logger.info("Fetching recent service metrics");
        return ResponseEntity.ok(orderProducerService.getRecentMetrics());
    }

    @GetMapping("/metrics/slowest")
    public ResponseEntity<List<ServiceMetrics>> getSlowestOperations() {
        logger.info("Fetching slowest operations");
        return ResponseEntity.ok(orderProducerService.getSlowestOperations());
    }

    @GetMapping("/metrics/summary")
    public ResponseEntity<Map<String, Object>> getMetricsSummary() {
        logger.info("Fetching metrics summary");
        Map<String, Object> response = new HashMap<>();
        response.put("totalMetrics", orderProducerService.getMetricsCount());
        response.put("successCount", orderProducerService.getMetricsCountByStatus("SUCCESS"));
        response.put("failureCount", orderProducerService.getMetricsCountByStatus("FAILURE"));
        response.put("avgCreateDurationMs", orderProducerService.getAverageDurationByOperationType("CREATE"));
        response.put("avgUpdateDurationMs", orderProducerService.getAverageDurationByOperationType("UPDATE"));
        response.put("avgSendKafkaDurationMs", orderProducerService.getAverageDurationByOperationType("SEND_KAFKA"));
        response.put("service", "SampleEventDrivenApplication - Order Producer Service");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/metrics/status/{status}")
    public ResponseEntity<List<ServiceMetrics>> getMetricsByStatus(@PathVariable String status) {
        logger.info("Fetching metrics by status: {}", status);
        return ResponseEntity.ok(orderProducerService.getMetricsByStatus(status));
    }
}
