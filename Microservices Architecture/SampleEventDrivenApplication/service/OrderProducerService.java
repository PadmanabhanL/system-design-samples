    @Transactional
    public void sendOrderEvent(OrderEvent orderEvent) {
        ServiceMetrics metrics = createAndStartMetrics(orderEvent);
        try {
            logger.info("Sending order event to Kafka: {}", orderEvent);
            Order order = saveOrderToDatabase(orderEvent);
            CompletableFuture<SendResult<String, OrderEvent>> future = sendOrderEventToKafka(orderEvent);
            handleKafkaSendResult(future, orderEvent);
            completeAndSaveMetrics(metrics, "SUCCESS", null);
        } catch (Exception e) {
            logger.error("Error in sendOrderEvent: {}", e.getMessage());
            completeAndSaveMetrics(metrics, "FAILURE", e.getMessage());
            throw e;
        }
    }

    private ServiceMetrics createAndStartMetrics(OrderEvent orderEvent) {
        ServiceMetrics metrics = new ServiceMetrics(
                SERVICE_NAME,
                "sendOrderEvent",
                "CREATE",
                "Order",
                orderEvent.getOrderId()
        );
        metrics.setAdditionalInfo("eventId=" + orderEvent.getEventId());
        return metrics;
    }

    private Order saveOrderToDatabase(OrderEvent orderEvent) {
        Order order = new Order(
                orderEvent.getEventId(),
                orderEvent.getOrderId(),
                orderEvent.getStatus()
        );
        orderRepository.save(order);
        logger.info("Order saved to database with eventId: {}", orderEvent.getEventId());
        return order;
    }

    private CompletableFuture<SendResult<String, OrderEvent>> sendOrderEventToKafka(OrderEvent orderEvent) {
        return kafkaTemplate.send(orderEventsTopic, orderEvent.getOrderId(), orderEvent);
    }

    private void handleKafkaSendResult(CompletableFuture<SendResult<String, OrderEvent>> future, OrderEvent orderEvent) {
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Order event sent successfully: {} with offset: {}",
                        orderEvent.getOrderId(),
                        result.getRecordMetadata().offset());
                updateOrderAsSent(orderEvent.getEventId());
            } else {
                logger.error("Failed to send order event: {}", orderEvent.getOrderId(), ex);
            }
        });
    }

    private void completeAndSaveMetrics(ServiceMetrics metrics, String status, String errorMsg) {
        if (errorMsg == null) {
            metrics.complete(status);
        } else {
            metrics.complete(status, errorMsg);
        }
        serviceMetricsRepository.save(metrics);
        logger.info("Metrics recorded: {} ms for sending order {}",
                metrics.getDurationMs(), metrics.getEntityId());
    }
// ...existing code...
