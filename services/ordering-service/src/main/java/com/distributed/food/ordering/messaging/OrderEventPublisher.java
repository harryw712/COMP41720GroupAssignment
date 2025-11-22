package com.distributed.food.ordering.messaging;

import com.distributed.food.ordering.model.RestaurantOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final KafkaTemplate<String, OrderEventPayload> kafkaTemplate;
    private final String createdTopic;
    private final String statusTopic;

    public OrderEventPublisher(KafkaTemplate<String, OrderEventPayload> kafkaTemplate,
                               @Value("${app.kafka.topics.order-created:orders.created}") String createdTopic,
                               @Value("${app.kafka.topics.order-status:orders.status}") String statusTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.createdTopic = createdTopic;
        this.statusTopic = statusTopic;
    }

    public void publishOrderCreated(RestaurantOrder order) {
        send(createdTopic, order, "ORDERING");
    }

    public void publishOrderStatusUpdated(RestaurantOrder order) {
        send(statusTopic, order, "ORDERING");
    }

    private void send(String topic, RestaurantOrder order, String source) {
        OrderEventPayload payload = new OrderEventPayload(
                order.getId(),
                order.getRestaurantId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getLatestStatusNote(),
                order.getCreatedAt(),
                source
        );
        kafkaTemplate.send(topic, order.getId().toString(), payload);
        LOGGER.info("Published {} event for order {}", topic, order.getId());
    }
}
