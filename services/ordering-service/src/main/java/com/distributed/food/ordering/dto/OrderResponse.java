package com.distributed.food.ordering.dto;

import com.distributed.food.ordering.model.OrderLineItem;
import com.distributed.food.ordering.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID restaurantId,
        String customerEmail,
        String customerName,
        OrderStatus status,
        String latestStatusNote,
        BigDecimal totalAmount,
        Instant createdAt,
        List<OrderLineItem> items
) {}
