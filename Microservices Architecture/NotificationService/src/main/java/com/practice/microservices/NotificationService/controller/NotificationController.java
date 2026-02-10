package com.practice.microservices.NotificationService.controller;

import com.practice.microservices.NotificationService.entity.Notification;
import com.practice.microservices.NotificationService.entity.ServiceMetrics;
import com.practice.microservices.NotificationService.service.EmailNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final EmailNotificationService emailNotificationService;

    public NotificationController(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        logger.info("Fetching all sent notifications");
        List<Notification> notifications = emailNotificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getNotificationCount() {
        logger.info("Fetching notification count");
        Map<String, Object> response = new HashMap<>();
        response.put("totalNotifications", emailNotificationService.getNotificationCount());
        response.put("service", "NotificationService - Email Notification Service");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "NotificationService");
        response.put("notificationsSent", emailNotificationService.getNotificationCount());
        return ResponseEntity.ok(response);
    }

    // ==================== METRICS ENDPOINTS ====================

    @GetMapping("/metrics")
    public ResponseEntity<List<ServiceMetrics>> getAllMetrics() {
        logger.info("Fetching all service metrics");
        return ResponseEntity.ok(emailNotificationService.getAllMetrics());
    }

    @GetMapping("/metrics/recent")
    public ResponseEntity<List<ServiceMetrics>> getRecentMetrics() {
        logger.info("Fetching recent service metrics");
        return ResponseEntity.ok(emailNotificationService.getRecentMetrics());
    }

    @GetMapping("/metrics/slowest")
    public ResponseEntity<List<ServiceMetrics>> getSlowestOperations() {
        logger.info("Fetching slowest operations");
        return ResponseEntity.ok(emailNotificationService.getSlowestOperations());
    }

    @GetMapping("/metrics/summary")
    public ResponseEntity<Map<String, Object>> getMetricsSummary() {
        logger.info("Fetching metrics summary");
        Map<String, Object> response = new HashMap<>();
        response.put("totalMetrics", emailNotificationService.getMetricsCount());
        response.put("successCount", emailNotificationService.getMetricsCountByStatus("SUCCESS"));
        response.put("failureCount", emailNotificationService.getMetricsCountByStatus("FAILURE"));
        response.put("skippedCount", emailNotificationService.getMetricsCountByStatus("SKIPPED"));
        response.put("avgCreateDurationMs", emailNotificationService.getAverageDurationByOperationType("CREATE"));
        response.put("service", "NotificationService - Email Notification Service");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/metrics/status/{status}")
    public ResponseEntity<List<ServiceMetrics>> getMetricsByStatus(@PathVariable String status) {
        logger.info("Fetching metrics by status: {}", status);
        return ResponseEntity.ok(emailNotificationService.getMetricsByStatus(status));
    }
}
