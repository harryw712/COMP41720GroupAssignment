package com.distributed.food.restaurant.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemResponse(UUID id, String name, BigDecimal price, boolean available) {
}
