package com.distributed.food.restaurant.controller;

import com.distributed.food.restaurant.service.MenuCatalogService;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/availability")
public class InternalAvailabilityController {

    private final MenuCatalogService menuCatalogService;

    public InternalAvailabilityController(MenuCatalogService menuCatalogService) {
        this.menuCatalogService = menuCatalogService;
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<Void> isAccepting(@PathVariable UUID restaurantId) {
        menuCatalogService.assertRestaurantAcceptsOrders(restaurantId);
        return ResponseEntity.ok().build();
    }
}
