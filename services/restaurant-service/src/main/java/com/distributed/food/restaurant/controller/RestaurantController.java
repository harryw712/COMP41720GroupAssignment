package com.distributed.food.restaurant.controller;

import com.distributed.food.restaurant.dto.CreateRestaurantRequest;
import com.distributed.food.restaurant.dto.MenuItemRequest;
import com.distributed.food.restaurant.dto.MenuItemResponse;
import com.distributed.food.restaurant.dto.RestaurantResponse;
import com.distributed.food.restaurant.service.MenuCatalogService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final MenuCatalogService menuCatalogService;

    public RestaurantController(MenuCatalogService menuCatalogService) {
        this.menuCatalogService = menuCatalogService;
    }

    @GetMapping
    public List<RestaurantResponse> listRestaurants() {
        return menuCatalogService.listRestaurants();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse createRestaurant(@Valid @RequestBody CreateRestaurantRequest request) {
        return menuCatalogService.createRestaurant(request);
    }

    @GetMapping("/{restaurantId}/menu")
    public List<MenuItemResponse> listMenu(@PathVariable UUID restaurantId) {
        return menuCatalogService.listMenuItems(restaurantId);
    }

    @PostMapping("/{restaurantId}/menu")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse addMenuItem(@PathVariable UUID restaurantId,
                                        @Valid @RequestBody MenuItemRequest request) {
        return menuCatalogService.createMenuItem(restaurantId, request);
    }
}
