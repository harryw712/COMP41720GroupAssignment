package com.distributed.food.restaurant.repository;

import com.distributed.food.restaurant.model.Restaurant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
}
