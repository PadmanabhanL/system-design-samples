package com.practice.microservices.NotificationService.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", unique = true)
    private String eventId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "notification_type")
    private String notificationType;

    @Column(name = "recipient")
    private String recipient;

    @Column(name = "message", length = 2000)
    private String message;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "status")
    private String status;  // SENT, FAILED, PENDING

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    public Notification() {
    }

    public Notification(String eventId, String orderId, String orderStatus, String notificationType, 
                        String recipient, String message) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.notificationType = notificationType;
        this.recipient = recipient;
        this.message = message;
        this.sentAt = LocalDateTime.now();
        this.status = "SENT";
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

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", notificationType='" + notificationType + '\'' +
                ", recipient='" + recipient + '\'' +
                ", sentAt=" + sentAt +
                ", status='" + status + '\'' +
                '}';
    }
}