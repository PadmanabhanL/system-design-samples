package com.practice.microservices.NotificationService.service;

import com.practice.microservices.NotificationService.entity.Notification;
import com.practice.microservices.NotificationService.entity.ServiceMetrics;
import com.practice.microservices.NotificationService.model.OrderEvent;
import com.practice.microservices.NotificationService.repository.NotificationRepository;
import com.practice.microservices.NotificationService.repository.ServiceMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmailNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);
    private static final String SERVICE_NAME = "EmailNotificationService";

    private final NotificationRepository notificationRepository;
    private final ServiceMetricsRepository serviceMetricsRepository;

    public EmailNotificationService(NotificationRepository notificationRepository,
                                     ServiceMetricsRepository serviceMetricsRepository) {
        this.notificationRepository = notificationRepository;
        this.serviceMetricsRepository = serviceMetricsRepository;
    }

    @KafkaListener(topics = "${app.kafka.topic.order-events}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void consumeOrderEvent(OrderEvent orderEvent) {
        String eventId = orderEvent.getEventId();
        
        // Start metrics tracking
        ServiceMetrics metrics = new ServiceMetrics(
                SERVICE_NAME, 
                "consumeOrderEvent", 
                "CREATE", 
                "Notification", 
                orderEvent.getOrderId()
        );
        metrics.setAdditionalInfo("eventId=" + eventId);
        
        // Idempotency check: Skip if notification was already sent for this event
        if (eventId != null && notificationRepository.existsByEventId(eventId)) {
            logger.info("Duplicate event detected, notification already sent: eventId={}, orderId={}", 
                    eventId, orderEvent.getOrderId());
            metrics.complete("SKIPPED", "Duplicate event - notification already sent");
            serviceMetricsRepository.save(metrics);
            return;
        }

        try {
            logger.info("========================================");
            logger.info("EMAIL NOTIFICATION SERVICE - Received Order Event");
            logger.info("========================================");
            logger.info("Event ID: {}", orderEvent.getEventId());
            logger.info("Order ID: {}", orderEvent.getOrderId());
            logger.info("Status: {}", orderEvent.getStatus());
            logger.info("Timestamp: {}", orderEvent.getTimestamp());
            logger.info("========================================");
            
            // Simulate sending email notification
            sendEmailNotification(orderEvent);
            
            // Complete metrics with success
            metrics.complete("SUCCESS");
            serviceMetricsRepository.save(metrics);
            logger.info("Metrics recorded: {} ms for sending notification for order {}", 
                    metrics.getDurationMs(), orderEvent.getOrderId());
                    
        } catch (Exception e) {
            logger.error("Error sending notification for order {}: {}", orderEvent.getOrderId(), e.getMessage());
            metrics.complete("FAILURE", e.getMessage());
            serviceMetricsRepository.save(metrics);
            throw e;
        }
    }

    private void sendEmailNotification(OrderEvent orderEvent) {
        // In a real application, this would integrate with an email service (SMTP, SendGrid, etc.)
        // For demonstration, we'll just log the email notification
        
        String recipient = "customer@example.com";
        String message = String.format(
                "Dear Customer, Your order %s has been %s. Order Details: Order ID: %s, Status: %s, Timestamp: %s. Thank you for your order!",
                orderEvent.getOrderId(), orderEvent.getStatus(), 
                orderEvent.getOrderId(), orderEvent.getStatus(), orderEvent.getTimestamp()
        );
        
        logger.info("╔══════════════════════════════════════════════════════════════╗");
        logger.info("║              SENDING EMAIL NOTIFICATION                       ║");
        logger.info("╠══════════════════════════════════════════════════════════════╣");
        logger.info("║ To:      customer@example.com                                 ║");
        logger.info("║ Subject: Order {} - Status Update", orderEvent.getOrderId());
        logger.info("╠══════════════════════════════════════════════════════════════╣");
        logger.info("║ Dear Customer,                                                ║");
        logger.info("║                                                               ║");
        logger.info("║ Your order {} has been {}.", orderEvent.getOrderId(), orderEvent.getStatus());
        logger.info("║                                                               ║");
        logger.info("║ Order Details:                                                ║");
        logger.info("║   - Order ID: {}", orderEvent.getOrderId());
        logger.info("║   - Status: {}", orderEvent.getStatus());
        logger.info("║   - Timestamp: {}", orderEvent.getTimestamp());
        logger.info("║                                                               ║");
        logger.info("║ Thank you for your order!                                     ║");
        logger.info("║                                                               ║");
        logger.info("║ Best regards,                                                 ║");
        logger.info("║ The Order Team                                                ║");
        logger.info("╚══════════════════════════════════════════════════════════════╝");
        
        // Save notification to database
        Notification notification = new Notification(
                orderEvent.getEventId(),
                orderEvent.getOrderId(),
                orderEvent.getStatus(),
                "EMAIL",
                recipient,
                message
        );
        notificationRepository.save(notification);
        
        logger.info("Email notification sent and saved to database for order: {}", orderEvent.getOrderId());
        logger.info("Total notifications sent: {}", notificationRepository.count());
    }

    // Get all notifications
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    // Get notification count
    public long getNotificationCount() {
        return notificationRepository.count();
    }

    // Get notifications by order ID
    public List<Notification> getNotificationsByOrderId(String orderId) {
        return notificationRepository.findByOrderId(orderId);
    }

    // Get notification by event ID
    public Notification getNotificationByEventId(String eventId) {
        return notificationRepository.findByEventId(eventId).orElse(null);
    }

    // Get notifications by status
    public List<Notification> getNotificationsByStatus(String status) {
        return notificationRepository.findByStatus(status);
    }

    // Check if notification was already sent (for idempotency)
    public boolean isNotificationSent(String eventId) {
        return notificationRepository.existsByEventId(eventId);
    }

    // Get count by status
    public long getCountByStatus(String status) {
        return notificationRepository.countByStatus(status);
    }

    // ==================== METRICS METHODS ====================

    // Get all metrics
    public List<ServiceMetrics> getAllMetrics() {
        return serviceMetricsRepository.findAll();
    }

    // Get metrics count
    public long getMetricsCount() {
        return serviceMetricsRepository.count();
    }

    // Get recent metrics
    public List<ServiceMetrics> getRecentMetrics() {
        return serviceMetricsRepository.findTop20ByOrderByStartTimeDesc();
    }

    // Get slowest operations
    public List<ServiceMetrics> getSlowestOperations() {
        return serviceMetricsRepository.findTop10ByOrderByDurationMsDesc();
    }

    // Get metrics by status
    public List<ServiceMetrics> getMetricsByStatus(String status) {
        return serviceMetricsRepository.findByStatus(status);
    }

    // Get average duration by operation type
    public Double getAverageDurationByOperationType(String operationType) {
        return serviceMetricsRepository.getAverageDurationByOperationType(operationType);
    }

    // Get count by status
    public long getMetricsCountByStatus(String status) {
        return serviceMetricsRepository.countByStatus(status);
    }
}