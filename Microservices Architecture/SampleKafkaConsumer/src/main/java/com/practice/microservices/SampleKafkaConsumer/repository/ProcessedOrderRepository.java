package com.practice.microservices.SampleKafkaConsumer.repository;

import com.practice.microservices.SampleKafkaConsumer.entity.ProcessedOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessedOrderRepository extends JpaRepository<ProcessedOrder, Long> {

    // Check if an event has already been processed (for idempotency)
    boolean existsByEventId(String eventId);

    // Find by event ID
    Optional<ProcessedOrder> findByEventId(String eventId);

    // Find by order ID
    List<ProcessedOrder> findByOrderId(String orderId);

    // Get all order IDs
    @Query("SELECT p.orderId FROM ProcessedOrder p")
    List<String> findAllOrderIds();

    // Count total processed orders
    long count();
}