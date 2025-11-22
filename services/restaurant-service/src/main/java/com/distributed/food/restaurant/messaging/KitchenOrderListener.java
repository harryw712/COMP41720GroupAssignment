package com.distributed.food.restaurant.messaging;

import com.distributed.food.restaurant.service.KitchenWorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class KitchenOrderListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(KitchenOrderListener.class);
    private final KitchenWorkflowService kitchenWorkflowService;

    public KitchenOrderListener(KitchenWorkflowService kitchenWorkflowService) {
        this.kitchenWorkflowService = kitchenWorkflowService;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.order-created:orders.created}",
            groupId = "restaurant-service",
            containerFactory = "orderCreatedListenerFactory")
    public void consume(@Payload OrderCreatedEvent event) {
        LOGGER.info("Received order {} for restaurant {}", event.orderId(), event.restaurantId());
        kitchenWorkflowService.handleOrderCreated(event);
    }
}
