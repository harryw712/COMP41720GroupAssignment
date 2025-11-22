package com.distributed.food.restaurant.dto;

import com.distributed.food.restaurant.model.KitchenStatus;
import jakarta.validation.constraints.NotNull;

public record KitchenStatusUpdateRequest(@NotNull KitchenStatus status, String note) {
}
