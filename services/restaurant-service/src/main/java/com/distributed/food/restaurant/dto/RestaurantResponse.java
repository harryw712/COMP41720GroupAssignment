package com.distributed.food.restaurant.dto;

import java.util.UUID;

public record RestaurantResponse(UUID id, String name, boolean acceptingOrders) {
}
