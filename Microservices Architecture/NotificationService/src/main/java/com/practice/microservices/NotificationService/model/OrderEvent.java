package com.practice.microservices.NotificationService.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OrderEvent implements Serializable {
    
    private String eventId;  // Unique ID for idempotency
    private String orderId;
    private String status;
    private LocalDateTime timestamp;

    public OrderEvent() {
    }

    public OrderEvent(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
        this.timestamp = LocalDateTime.now();
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "OrderEvent{" +
                "eventId='" + eventId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
