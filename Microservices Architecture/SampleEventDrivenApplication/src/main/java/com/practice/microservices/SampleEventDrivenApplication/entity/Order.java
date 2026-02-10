package com.practice.microservices.SampleEventDrivenApplication.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", unique = true, nullable = false)
    private String eventId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "sent_to_kafka")
    private boolean sentToKafka = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    public Order() {
    }

    public Order(String eventId, String orderId, String status) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.sentToKafka = false;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isSentToKafka() {
        return sentToKafka;
    }

    public void setSentToKafka(boolean sentToKafka) {
        this.sentToKafka = sentToKafka;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", sentToKafka=" + sentToKafka +
                ", sentAt=" + sentAt +
                '}';
    }
}