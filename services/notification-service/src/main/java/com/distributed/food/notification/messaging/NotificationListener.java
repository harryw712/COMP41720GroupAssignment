package com.distributed.food.notification.messaging;

import com.distributed.food.notification.service.NotificationOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationListener.class);
    private final NotificationOrchestrator orchestrator;

    public NotificationListener(NotificationOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.order-status:orders.status}",
            groupId = "notification-service",
            containerFactory = "orderStatusListenerFactory")
    public void consume(@Payload OrderStatusEvent event) {
        LOGGER.info("Order {} changed status to {}", event.orderId(), event.status());
        orchestrator.handleStatusEvent(event);
    }
}
