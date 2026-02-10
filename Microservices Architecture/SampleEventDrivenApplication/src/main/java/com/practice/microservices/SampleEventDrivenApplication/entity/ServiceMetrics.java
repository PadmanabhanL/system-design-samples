package com.practice.microservices.SampleEventDrivenApplication.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_metrics")
public class ServiceMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "method_name", nullable = false)
    private String methodName;

    @Column(name = "operation_type")
    private String operationType;  // CREATE, UPDATE, DELETE, READ, SEND_KAFKA

    @Column(name = "entity_type")
    private String entityType;  // Order, OrderEvent, etc.

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "status")
    private String status;  // SUCCESS, FAILURE, PENDING

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "additional_info", length = 1000)
    private String additionalInfo;

    public ServiceMetrics() {
    }

    public ServiceMetrics(String serviceName, String methodName, String operationType, 
                          String entityType, String entityId) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.operationType = operationType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.startTime = LocalDateTime.now();
    }

    public void complete(String status) {
        this.endTime = LocalDateTime.now();
        this.status = status;
        if (this.startTime != null && this.endTime != null) {
            this.durationMs = java.time.Duration.between(startTime, endTime).toMillis();
        }
    }

    public void complete(String status, String errorMessage) {
        complete(status);
        this.errorMessage = errorMessage;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
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

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public String toString() {
        return "ServiceMetrics{" +
                "id=" + id +
                ", serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", operationType='" + operationType + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", durationMs=" + durationMs +
                ", status='" + status + '\'' +
                '}';
    }
}