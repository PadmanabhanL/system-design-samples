package com.practice.microservices.SampleKafkaConsumer.repository;

import com.practice.microservices.SampleKafkaConsumer.entity.FailedOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FailedOrderRepository extends JpaRepository<FailedOrder, Long> {

    // Check if an event has already failed (for idempotency)
    boolean existsByEventId(String eventId);

    // Find by event ID
    Optional<FailedOrder> findByEventId(String eventId);

    // Find by order ID
    List<FailedOrder> findByOrderId(String orderId);

    // Get all failed order IDs
    @Query("SELECT f.orderId FROM FailedOrder f")
    List<String> findAllOrderIds();

    // Count total failed orders
    long count();

    // Find orders that can be retried (retry count less than max)
    List<FailedOrder> findByRetryCountLessThan(int maxRetryCount);
}