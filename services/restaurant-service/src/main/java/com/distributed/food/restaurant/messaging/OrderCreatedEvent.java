package com.distributed.food.restaurant.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID restaurantId,
        BigDecimal totalAmount,
        Instant createdAt,
        String source
) {}
