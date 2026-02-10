package com.practice.microservices.SampleKafkaConsumer.service;

import com.practice.microservices.SampleKafkaConsumer.entity.FailedOrder;
import com.practice.microservices.SampleKafkaConsumer.entity.ProcessedOrder;
import com.practice.microservices.SampleKafkaConsumer.entity.ServiceMetrics;
import com.practice.microservices.SampleKafkaConsumer.model.OrderEvent;
import com.practice.microservices.SampleKafkaConsumer.repository.FailedOrderRepository;
import com.practice.microservices.SampleKafkaConsumer.repository.ProcessedOrderRepository;
import com.practice.microservices.SampleKafkaConsumer.repository.ServiceMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderTrackingService {

    private static final Logger logger = LoggerFactory.getLogger(OrderTrackingService.class);
    private static final String SERVICE_NAME = "OrderTrackingService";

    private final ProcessedOrderRepository processedOrderRepository;
    private final FailedOrderRepository failedOrderRepository;
    private final ServiceMetricsRepository serviceMetricsRepository;

    public OrderTrackingService(ProcessedOrderRepository processedOrderRepository, 
                                 FailedOrderRepository failedOrderRepository,
                                 ServiceMetricsRepository serviceMetricsRepository) {
        this.processedOrderRepository = processedOrderRepository;
        this.failedOrderRepository = failedOrderRepository;
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
                "ProcessedOrder", 
                orderEvent.getOrderId()
        );
        metrics.setAdditionalInfo("eventId=" + eventId);
        
        // Idempotency check: Skip if event was already processed
        if (eventId != null && processedOrderRepository.existsByEventId(eventId)) {
            logger.info("Duplicate event detected, skipping: eventId={}, orderId={}", 
                    eventId, orderEvent.getOrderId());
            metrics.complete("SKIPPED", "Duplicate event - already processed");
            serviceMetricsRepository.save(metrics);
            return;
        }

        // Also check if it was already in failed orders
        if (eventId != null && failedOrderRepository.existsByEventId(eventId)) {
            logger.info("Event already in failed orders, skipping: eventId={}, orderId={}", 
                    eventId, orderEvent.getOrderId());
            metrics.complete("SKIPPED", "Duplicate event - already in failed orders");
            serviceMetricsRepository.save(metrics);
            return;
        }

        try {
            // Bug: Throw RuntimeException when orderId ends with "5"
            if (orderEvent.getOrderId() != null && orderEvent.getOrderId().endsWith("5")) {
                throw new RuntimeException("Invalid input: Order ID ending with 5 is not allowed");
            }
            
            logger.info("========================================");
            logger.info("ORDER TRACKING SERVICE - Received Order Event");
            logger.info("========================================");
            logger.info("Event ID: {}", orderEvent.getEventId());
            logger.info("Order ID: {}", orderEvent.getOrderId());
            logger.info("Status: {}", orderEvent.getStatus());
            logger.info("Timestamp: {}", orderEvent.getTimestamp());
            logger.info("========================================");
            
            // Save to database
            ProcessedOrder processedOrder = new ProcessedOrder(
                    orderEvent.getEventId(),
                    orderEvent.getOrderId(),
                    orderEvent.getStatus(),
                    orderEvent.getTimestamp()
            );
            processedOrderRepository.save(processedOrder);
            
            logger.info("Order {} has been saved to database. Total orders tracked: {}", 
                    orderEvent.getOrderId(), processedOrderRepository.count());
            
            // Complete metrics with success
            metrics.complete("SUCCESS");
            serviceMetricsRepository.save(metrics);
            logger.info("Metrics recorded: {} ms for processing order {}", 
                    metrics.getDurationMs(), orderEvent.getOrderId());
                    
        } catch (RuntimeException e) {
            logger.error("Error processing order {}: {}", orderEvent.getOrderId(), e.getMessage());
            
            // Save to failed orders table
            FailedOrder failedOrder = new FailedOrder(
                    orderEvent.getEventId(),
                    orderEvent.getOrderId(),
                    orderEvent.getStatus(),
                    orderEvent.getTimestamp(),
                    e.getMessage()
            );
            failedOrderRepository.save(failedOrder);
            
            logger.warn("Order {} has been saved to failed orders table. Total failed orders: {}", 
                    orderEvent.getOrderId(), failedOrderRepository.count());
            
            // Complete metrics with failure
            metrics.setOperationType("CREATE_FAILED");
            metrics.setEntityType("FailedOrder");
            metrics.complete("FAILURE", e.getMessage());
            serviceMetricsRepository.save(metrics);
        }
    }

    // Get all processed orders
    public List<ProcessedOrder> getSubmittedOrders() {
        return processedOrderRepository.findAll();
    }

    // Get count of processed orders
    public long getOrderCount() {
        return processedOrderRepository.count();
    }

    // Get processed order by order ID
    public List<ProcessedOrder> getOrdersByOrderId(String orderId) {
        return processedOrderRepository.findByOrderId(orderId);
    }

    // Get processed order by event ID
    public ProcessedOrder getOrderByEventId(String eventId) {
        return processedOrderRepository.findByEventId(eventId).orElse(null);
    }

    // Get all failed orders
    public List<FailedOrder> getFailedOrders() {
        return failedOrderRepository.findAll();
    }

    // Get count of failed orders
    public long getFailedOrderCount() {
        return failedOrderRepository.count();
    }

    // Get all failed order IDs
    public List<String> getFailedOrderIds() {
        return failedOrderRepository.findAllOrderIds();
    }

    // Get failed order by event ID
    public FailedOrder getFailedOrderByEventId(String eventId) {
        return failedOrderRepository.findByEventId(eventId).orElse(null);
    }

    // Check if event was already processed (for idempotency)
    public boolean isEventProcessed(String eventId) {
        return processedOrderRepository.existsByEventId(eventId) || 
               failedOrderRepository.existsByEventId(eventId);
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