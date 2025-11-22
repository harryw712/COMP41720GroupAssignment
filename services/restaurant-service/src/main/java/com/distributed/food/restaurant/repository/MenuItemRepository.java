package com.distributed.food.restaurant.repository;

import com.distributed.food.restaurant.model.MenuItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByRestaurant_Id(UUID restaurantId);
}
