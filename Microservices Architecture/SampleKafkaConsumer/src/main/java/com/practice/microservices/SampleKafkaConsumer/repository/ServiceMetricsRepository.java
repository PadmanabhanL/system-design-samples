package com.practice.microservices.SampleKafkaConsumer.repository;

import com.practice.microservices.SampleKafkaConsumer.entity.ServiceMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceMetricsRepository extends JpaRepository<ServiceMetrics, Long> {

    List<ServiceMetrics> findByServiceName(String serviceName);

    List<ServiceMetrics> findByMethodName(String methodName);

    List<ServiceMetrics> findByOperationType(String operationType);

    List<ServiceMetrics> findByStatus(String status);

    List<ServiceMetrics> findByEntityId(String entityId);

    @Query("SELECT AVG(m.durationMs) FROM ServiceMetrics m WHERE m.methodName = ?1")
    Double getAverageDurationByMethod(String methodName);

    @Query("SELECT AVG(m.durationMs) FROM ServiceMetrics m WHERE m.operationType = ?1")
    Double getAverageDurationByOperationType(String operationType);

    long countByStatus(String status);

    long countByOperationType(String operationType);

    List<ServiceMetrics> findTop10ByOrderByDurationMsDesc();

    List<ServiceMetrics> findTop20ByOrderByStartTimeDesc();
}