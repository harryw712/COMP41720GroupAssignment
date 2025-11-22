package com.distributed.food.ordering.messaging;

import com.distributed.food.ordering.model.OrderStatus;
import java.util.UUID;

public record OrderStatusMessage(UUID orderId, OrderStatus status, String note) {
}
