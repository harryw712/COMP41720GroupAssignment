package com.distributed.food.restaurant.messaging;

import java.util.UUID;

public record KitchenStatusEvent(UUID orderId, String status, String note) {
}
