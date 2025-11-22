package com.distributed.food.ordering.messaging;

import com.distributed.food.ordering.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventsListener.class);
    private final OrderService orderService;

    public OrderEventsListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.kitchen-status:restaurant.status}",
            groupId = "ordering-service",
            containerFactory = "orderStatusListenerFactory")
    public void handleKitchenStatus(@Payload OrderStatusMessage message) {
        LOGGER.info("Applying kitchen status {} for order {}", message.status(), message.orderId());
        orderService.applyStatusFromMessage(message.orderId(), message.status(), message.note());
    }
}
