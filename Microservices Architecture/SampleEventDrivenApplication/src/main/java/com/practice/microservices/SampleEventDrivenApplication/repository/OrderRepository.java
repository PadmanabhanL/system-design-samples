package com.practice.microservices.SampleEventDrivenApplication.repository;

import com.practice.microservices.SampleEventDrivenApplication.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find by event ID
    Optional<Order> findByEventId(String eventId);

    // Find by order ID
    List<Order> findByOrderId(String orderId);

    // Check if event ID exists
    boolean existsByEventId(String eventId);

    // Find orders not yet sent to Kafka
    List<Order> findBySentToKafkaFalse();

    // Find orders sent to Kafka
    List<Order> findBySentToKafkaTrue();

    // Get all order IDs
    @Query("SELECT o.orderId FROM Order o")
    List<String> findAllOrderIds();

    // Count orders by status
    long countByStatus(String status);
}