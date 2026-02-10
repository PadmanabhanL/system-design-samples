package com.practice.microservices.SampleKafkaConsumer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "processed_orders")
public class ProcessedOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", unique = true, nullable = false)
    private String eventId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "status")
    private String status;

    @Column(name = "event_timestamp")
    private LocalDateTime eventTimestamp;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public ProcessedOrder() {
    }

    public ProcessedOrder(String eventId, String orderId, String status, LocalDateTime eventTimestamp) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.status = status;
        this.eventTimestamp = eventTimestamp;
        this.processedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    @Override
    public String toString() {
        return "ProcessedOrder{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", status='" + status + '\'' +
                ", eventTimestamp=" + eventTimestamp +
                ", processedAt=" + processedAt +
                '}';
    }
}