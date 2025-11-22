package com.distributed.food.notification.messaging;

import java.util.UUID;

public record OrderStatusEvent(UUID orderId, String status, String note) {
}
