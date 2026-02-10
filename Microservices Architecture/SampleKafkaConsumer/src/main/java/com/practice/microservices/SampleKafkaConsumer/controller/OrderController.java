package com.practice.microservices.SampleKafkaConsumer.controller;

import com.practice.microservices.SampleKafkaConsumer.entity.FailedOrder;
import com.practice.microservices.SampleKafkaConsumer.entity.ProcessedOrder;
import com.practice.microservices.SampleKafkaConsumer.entity.ServiceMetrics;
import com.practice.microservices.SampleKafkaConsumer.service.OrderTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderTrackingService orderTrackingService;

    public OrderController(OrderTrackingService orderTrackingService) {
        this.orderTrackingService = orderTrackingService;
    }

    @GetMapping
    public ResponseEntity<List<ProcessedOrder>> getAllOrders() {
        logger.info("Fetching all submitted orders from database");
        List<ProcessedOrder> orders = orderTrackingService.getSubmittedOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getOrderCount() {
        logger.info("Fetching order count from database");
        Map<String, Object> response = new HashMap<>();
        response.put("totalOrders", orderTrackingService.getOrderCount());
        response.put("totalFailedOrders", orderTrackingService.getFailedOrderCount());
        response.put("service", "SampleKafkaConsumer - Order Tracking Service");
        response.put("storage", "SQL Server Database");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/failed")
    public ResponseEntity<List<FailedOrder>> getFailedOrders() {
        logger.info("Fetching all failed/unprocessed orders from database");
        List<FailedOrder> failedOrders = orderTrackingService.getFailedOrders();
        return ResponseEntity.ok(failedOrders);
    }

    @GetMapping("/failed/ids")
    public ResponseEntity<Map<String, Object>> getFailedOrderIds() {
        logger.info("Fetching failed order IDs from database");
        Map<String, Object> response = new HashMap<>();
        response.put("failedOrderIds", orderTrackingService.getFailedOrderIds());
        response.put("totalFailedOrders", orderTrackingService.getFailedOrderCount());
        response.put("service", "SampleKafkaConsumer - Order Tracking Service");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/failed/count")
    public ResponseEntity<Map<String, Object>> getFailedOrderCount() {
        logger.info("Fetching failed order count from database");
        Map<String, Object> response = new HashMap<>();
        response.put("totalFailedOrders", orderTrackingService.getFailedOrderCount());
        response.put("service", "SampleKafkaConsumer - Order Tracking Service");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getOrderById(@PathVariable String orderId) {
        logger.info("Fetching order by ID: {}", orderId);
        List<ProcessedOrder> orders = orderTrackingService.getOrdersByOrderId(orderId);
        
        if (!orders.isEmpty()) {
            return ResponseEntity.ok(orders);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "not_found");
            response.put("message", "Order not found with ID: " + orderId);
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<Object> getOrderByEventId(@PathVariable String eventId) {
        logger.info("Fetching order by Event ID: {}", eventId);
        ProcessedOrder order = orderTrackingService.getOrderByEventId(eventId);
        
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            // Check if it's in failed orders
            FailedOrder failedOrder = orderTrackingService.getFailedOrderByEventId(eventId);
            if (failedOrder != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "failed");
                response.put("message", "Order processing failed");
                response.put("failedOrder", failedOrder);
                return ResponseEntity.ok(response);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "not_found");
            response.put("message", "Event not found with ID: " + eventId);
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/idempotency/check/{eventId}")
    public ResponseEntity<Map<String, Object>> checkIdempotency(@PathVariable String eventId) {
        logger.info("Checking idempotency for Event ID: {}", eventId);
        boolean isProcessed = orderTrackingService.isEventProcessed(eventId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("eventId", eventId);
        response.put("isProcessed", isProcessed);
        response.put("message", isProcessed ? "Event has already been processed" : "Event has not been processed yet");
        return ResponseEntity.ok(response);
    }

    // ==================== METRICS ENDPOINTS ====================

    @GetMapping("/metrics")
    public ResponseEntity<List<ServiceMetrics>> getAllMetrics() {
        logger.info("Fetching all service metrics");
        return ResponseEntity.ok(orderTrackingService.getAllMetrics());
    }

    @GetMapping("/metrics/recent")
    public ResponseEntity<List<ServiceMetrics>> getRecentMetrics() {
        logger.info("Fetching recent service metrics");
        return ResponseEntity.ok(orderTrackingService.getRecentMetrics());
    }

    @GetMapping("/metrics/slowest")
    public ResponseEntity<List<ServiceMetrics>> getSlowestOperations() {
        logger.info("Fetching slowest operations");
        return ResponseEntity.ok(orderTrackingService.getSlowestOperations());
    }

    @GetMapping("/metrics/summary")
    public ResponseEntity<Map<String, Object>> getMetricsSummary() {
        logger.info("Fetching metrics summary");
        Map<String, Object> response = new HashMap<>();
        response.put("totalMetrics", orderTrackingService.getMetricsCount());
        response.put("successCount", orderTrackingService.getMetricsCountByStatus("SUCCESS"));
        response.put("failureCount", orderTrackingService.getMetricsCountByStatus("FAILURE"));
        response.put("skippedCount", orderTrackingService.getMetricsCountByStatus("SKIPPED"));
        response.put("avgCreateDurationMs", orderTrackingService.getAverageDurationByOperationType("CREATE"));
        response.put("service", "SampleKafkaConsumer - Order Tracking Service");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/metrics/status/{status}")
    public ResponseEntity<List<ServiceMetrics>> getMetricsByStatus(@PathVariable String status) {
        logger.info("Fetching metrics by status: {}", status);
        return ResponseEntity.ok(orderTrackingService.getMetricsByStatus(status));
    }
}
