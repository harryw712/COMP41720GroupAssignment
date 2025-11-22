package com.distributed.food.restaurant.service;

import com.distributed.food.restaurant.dto.KitchenStatusUpdateRequest;
import com.distributed.food.restaurant.messaging.KitchenStatusEvent;
import com.distributed.food.restaurant.messaging.OrderCreatedEvent;
import com.distributed.food.restaurant.model.KitchenStatus;
import com.distributed.food.restaurant.model.OrderTicket;
import com.distributed.food.restaurant.model.Restaurant;
import com.distributed.food.restaurant.repository.OrderTicketRepository;
import com.distributed.food.restaurant.repository.RestaurantRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KitchenWorkflowService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KitchenWorkflowService.class);

    private final OrderTicketRepository ticketRepository;
    private final RestaurantRepository restaurantRepository;
    private final KafkaTemplate<String, KitchenStatusEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String kitchenStatusTopic;

    public KitchenWorkflowService(OrderTicketRepository ticketRepository,
                                  RestaurantRepository restaurantRepository,
                                  KafkaTemplate<String, KitchenStatusEvent> kafkaTemplate,
                                  ObjectMapper objectMapper,
                                  @Value("${app.kafka.topics.kitchen-status:restaurant.status}") String kitchenStatusTopic) {
        this.ticketRepository = ticketRepository;
        this.restaurantRepository = restaurantRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.kitchenStatusTopic = kitchenStatusTopic;
    }

    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        Restaurant restaurant = restaurantRepository.findById(event.restaurantId())
                .orElseThrow(() -> new IllegalStateException("Restaurant not registered: " + event.restaurantId()));
        OrderTicket ticket = ticketRepository.findByOrderId(event.orderId()).orElseGet(OrderTicket::new);
        ticket.setOrderId(event.orderId());
        ticket.setRestaurant(restaurant);
        ticket.setStatus(KitchenStatus.RECEIVED);
        ticket.setLastUpdated(Instant.now());
        ticket.setNote("Order accepted");
        ticket.setPayload(writePayload(event));
        ticketRepository.save(ticket);
        publishStatus(ticket);
    }

    @Transactional
    public OrderTicket updateStatus(UUID orderId, KitchenStatusUpdateRequest request) {
        OrderTicket ticket = ticketRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("Ticket not found: " + orderId));
        ticket.setStatus(request.status());
        ticket.setNote(request.note());
        ticket.setLastUpdated(Instant.now());
        OrderTicket saved = ticketRepository.save(ticket);
        publishStatus(saved);
        return saved;
    }

    private void publishStatus(OrderTicket ticket) {
        KitchenStatusEvent event = new KitchenStatusEvent(
                ticket.getOrderId(),
                mapToOrderStatus(ticket.getStatus()),
                ticket.getNote());
        kafkaTemplate.send(kitchenStatusTopic, ticket.getOrderId().toString(), event);
        LOGGER.info("Emitted kitchen status {} for order {}", ticket.getStatus(), ticket.getOrderId());
    }

    private String writePayload(OrderCreatedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize order payload", e);
        }
    }

    private String mapToOrderStatus(KitchenStatus status) {
        return switch (status) {
            case RECEIVED, PREPARING -> "IN_PREPARATION";
            case READY -> "READY_FOR_DELIVERY";
            case DISPATCHED -> "COMPLETED";
            case FAILED -> "FAILED";
        };
    }
}
