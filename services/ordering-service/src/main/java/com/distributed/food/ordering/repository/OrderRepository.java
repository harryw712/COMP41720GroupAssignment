package com.distributed.food.ordering.repository;

import com.distributed.food.ordering.model.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<RestaurantOrder, UUID> {
}
