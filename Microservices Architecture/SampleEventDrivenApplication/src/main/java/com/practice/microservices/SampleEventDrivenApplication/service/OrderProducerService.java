package com.practice.microservices.SampleEventDrivenApplication.service;

import com.practice.microservices.SampleEventDrivenApplication.entity.Order;
import com.practice.microservices.SampleEventDrivenApplication.entity.ServiceMetrics;
import com.practice.microservices.SampleEventDrivenApplication.model.OrderEvent;
import com.practice.microservices.SampleEventDrivenApplication.repository.OrderRepository;
import com.practice.microservices.SampleEventDrivenApplication.repository.ServiceMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderProducerService {

    private static final Logger logger = LoggerFactory.getLogger(OrderProducerService.class);
    private static final String SERVICE_NAME = "OrderProducerService";

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final OrderRepository orderRepository;
    private final ServiceMetricsRepository serviceMetricsRepository;

    @Value("${app.kafka.topic.order-events}")
    private String orderEventsTopic;

    public OrderProducerService(KafkaTemplate<String, OrderEvent> kafkaTemplate, 
                                 OrderRepository orderRepository,
                                 ServiceMetricsRepository serviceMetricsRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderRepository = orderRepository;
        this.serviceMetricsRepository = serviceMetricsRepository;
    }

    @Transactional
    public void sendOrderEvent(OrderEvent orderEvent) {
        ServiceMetrics metrics = createAndStartMetrics(orderEvent);
        try {
            logger.info("Sending order event to Kafka: {}", orderEvent);
            Order order = saveOrderToDatabase(orderEvent);
            CompletableFuture<SendResult<String, OrderEvent>> future = sendOrderEventToKafka(orderEvent);
            handleKafkaSendResult(future, orderEvent);
            completeAndSaveMetrics(metrics, "SUCCESS", null);
        } catch (Exception e) {
            logger.error("Error in sendOrderEvent: {}", e.getMessage());
            completeAndSaveMetrics(metrics, "FAILURE", e.getMessage());
            throw e;
        }
    }

    private ServiceMetrics createAndStartMetrics(OrderEvent orderEvent) {
        ServiceMetrics metrics = new ServiceMetrics(
                SERVICE_NAME,
                "sendOrderEvent",
                "CREATE",
                "Order",
                orderEvent.getOrderId()
        );
        metrics.setAdditionalInfo("eventId=" + orderEvent.getEventId());
        return metrics;
    }

    private Order saveOrderToDatabase(OrderEvent orderEvent) {
        Order order = new Order(
                orderEvent.getEventId(),
                orderEvent.getOrderId(),
                orderEvent.getStatus()
        );
        orderRepository.save(order);
        logger.info("Order saved to database with eventId: {}", orderEvent.getEventId());
        return order;
    }

    private CompletableFuture<SendResult<String, OrderEvent>> sendOrderEventToKafka(OrderEvent orderEvent) {
        return kafkaTemplate.send(orderEventsTopic, orderEvent.getOrderId(), orderEvent);
    }

    private void handleKafkaSendResult(CompletableFuture<SendResult<String, OrderEvent>> future, OrderEvent orderEvent) {
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Order event sent successfully: {} with offset: {}",
                        orderEvent.getOrderId(),
                        result.getRecordMetadata().offset());
                updateOrderAsSent(orderEvent.getEventId());
            } else {
                logger.error("Failed to send order event: {}", orderEvent.getOrderId(), ex);
            }
        });
    }

    private void completeAndSaveMetrics(ServiceMetrics metrics, String status, String errorMsg) {
        if (errorMsg == null) {
            metrics.complete(status);
        } else {
            metrics.complete(status, errorMsg);
        }
        serviceMetricsRepository.save(metrics);
        logger.info("Metrics recorded: {} ms for sending order {}",
                metrics.getDurationMs(), metrics.getEntityId());
    }

    @Transactional
    public void updateOrderAsSent(String eventId) {
        // Start metrics tracking
        ServiceMetrics metrics = new ServiceMetrics(
                SERVICE_NAME, 
                "updateOrderAsSent", 
                "UPDATE", 
                "Order", 
                eventId
        );
        
        try {
            orderRepository.findByEventId(eventId).ifPresent(order -> {
                order.setSentToKafka(true);
                order.setSentAt(LocalDateTime.now());
                orderRepository.save(order);
                logger.info("Order marked as sent to Kafka: {}", eventId);
            });
            
            metrics.complete("SUCCESS");
            serviceMetricsRepository.save(metrics);
        } catch (Exception e) {
            metrics.complete("FAILURE", e.getMessage());
            serviceMetricsRepository.save(metrics);
            throw e;
        }
    }

    // Get all orders from database
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get order count
    public long getOrderCount() {
        return orderRepository.count();
    }

    // Get orders by order ID
    public List<Order> getOrdersByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    // Get order by event ID
    public Order getOrderByEventId(String eventId) {
        return orderRepository.findByEventId(eventId).orElse(null);
    }

    // Get orders not sent to Kafka
    public List<Order> getUnsentOrders() {
        return orderRepository.findBySentToKafkaFalse();
    }

    // Get orders sent to Kafka
    public List<Order> getSentOrders() {
        return orderRepository.findBySentToKafkaTrue();
    }

    /**
     * Send order event to Kafka WITHOUT saving to database.
     * This is used for idempotency simulation to send duplicate messages.
     */
    public void sendOrderEventWithoutSaving(OrderEvent orderEvent) {
        // Start metrics tracking
        ServiceMetrics metrics = new ServiceMetrics(
                SERVICE_NAME, 
                "sendOrderEventWithoutSaving", 
                "SEND_KAFKA", 
                "OrderEvent", 
                orderEvent.getOrderId()
        );
        metrics.setAdditionalInfo("eventId=" + orderEvent.getEventId() + " (idempotency simulation)");
        
        try {
            logger.info("Sending order event to Kafka (without saving): eventId={}, orderId={}", 
                    orderEvent.getEventId(), orderEvent.getOrderId());
            
            CompletableFuture<SendResult<String, OrderEvent>> future = 
                    kafkaTemplate.send(orderEventsTopic, orderEvent.getOrderId(), orderEvent);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Order event sent successfully: {} with offset: {}", 
                            orderEvent.getOrderId(), 
                            result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send order event: {}", orderEvent.getOrderId(), ex);
                }
            });
            
            metrics.complete("SUCCESS");
            serviceMetricsRepository.save(metrics);
        } catch (Exception e) {
            metrics.complete("FAILURE", e.getMessage());
            serviceMetricsRepository.save(metrics);
            throw e;
        }
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