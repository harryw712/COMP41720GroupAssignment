package com.distributed.food.ordering.dto;

import com.distributed.food.ordering.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status, String note) {
}
