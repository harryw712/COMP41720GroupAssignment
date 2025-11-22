package com.distributed.food.notification.service;

import com.distributed.food.notification.messaging.OrderStatusEvent;
import com.distributed.food.notification.model.NotificationRecord;
import com.distributed.food.notification.model.NotificationStatus;
import com.distributed.food.notification.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationOrchestrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationOrchestrator.class);
    private final NotificationRepository repository;
    private final ObjectMapper objectMapper;

    public NotificationOrchestrator(NotificationRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void handleStatusEvent(OrderStatusEvent event) {
        List.of("EMAIL", "PUSH").forEach(channel -> persistNotification(event, channel));
    }

    public List<NotificationRecord> listAll() {
        return repository.findAll();
    }

    public List<NotificationRecord> listByOrderId(UUID orderId) {
        return repository.findByOrderId(orderId);
    }

    private void persistNotification(OrderStatusEvent event, String channel) {
        NotificationRecord record = new NotificationRecord();
        record.setOrderId(event.orderId());
        record.setChannel(channel);
        record.setPayload(writePayload(event));
        try {
            simulateDelivery(record);
        } catch (Exception exception) {
            LOGGER.error("Notification delivery failure for {}", event.orderId(), exception);
            record.setStatus(NotificationStatus.FAILED);
        }
        repository.save(record);
    }

    private void simulateDelivery(NotificationRecord record) {
        record.setStatus(NotificationStatus.SENT);
        record.setDeliveredAt(Instant.now());
    }

    private String writePayload(OrderStatusEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize payload", e);
        }
    }
}
