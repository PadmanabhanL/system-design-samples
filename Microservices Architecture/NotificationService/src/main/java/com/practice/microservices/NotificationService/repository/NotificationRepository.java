package com.practice.microservices.NotificationService.repository;

import com.practice.microservices.NotificationService.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Check if notification for event already exists (for idempotency)
    boolean existsByEventId(String eventId);

    // Find by event ID
    Optional<Notification> findByEventId(String eventId);

    // Find by order ID
    List<Notification> findByOrderId(String orderId);

    // Find by status
    List<Notification> findByStatus(String status);

    // Find by notification type
    List<Notification> findByNotificationType(String notificationType);

    // Get all order IDs that have notifications
    @Query("SELECT DISTINCT n.orderId FROM Notification n")
    List<String> findAllOrderIds();

    // Count by status
    long countByStatus(String status);

    // Find failed notifications
    List<Notification> findByStatusOrderBySentAtDesc(String status);
}