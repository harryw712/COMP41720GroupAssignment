package com.distributed.food.ordering.messaging;

import com.distributed.food.ordering.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderEventPayload(
        UUID orderId,
        UUID restaurantId,
        OrderStatus status,
        BigDecimal totalAmount,
        String note,
        Instant createdAt,
        String source
) {}
