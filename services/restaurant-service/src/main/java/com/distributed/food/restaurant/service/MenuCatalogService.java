package com.distributed.food.restaurant.service;

import com.distributed.food.restaurant.dto.CreateRestaurantRequest;
import com.distributed.food.restaurant.dto.MenuItemRequest;
import com.distributed.food.restaurant.dto.MenuItemResponse;
import com.distributed.food.restaurant.dto.RestaurantResponse;
import com.distributed.food.restaurant.model.MenuItem;
import com.distributed.food.restaurant.model.Restaurant;
import com.distributed.food.restaurant.repository.MenuItemRepository;
import com.distributed.food.restaurant.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MenuCatalogService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public MenuCatalogService(RestaurantRepository restaurantRepository,
                              MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public List<RestaurantResponse> listRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(restaurant -> new RestaurantResponse(restaurant.getId(), restaurant.getName(), restaurant.isAcceptingOrders()))
                .toList();
    }

    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        Restaurant saved = restaurantRepository.save(restaurant);
        return new RestaurantResponse(saved.getId(), saved.getName(), saved.isAcceptingOrders());
    }

    @Transactional
    public MenuItemResponse createMenuItem(UUID restaurantId, MenuItemRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalStateException("Restaurant not found: " + restaurantId));
        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant);
        menuItem.setName(request.getName());
        menuItem.setPrice(request.getPrice());
        menuItem.setAvailable(request.isAvailable());
        MenuItem saved = menuItemRepository.save(menuItem);
        return toResponse(saved);
    }

    public List<MenuItemResponse> listMenuItems(UUID restaurantId) {
        return menuItemRepository.findByRestaurant_Id(restaurantId).stream()
                .map(this::toResponse)
                .toList();
    }

    public void assertRestaurantAcceptsOrders(UUID restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalStateException("Restaurant not found: " + restaurantId));
        if (!restaurant.isAcceptingOrders()) {
            throw new IllegalStateException("Restaurant temporarily disabled ordering");
        }
    }

    private MenuItemResponse toResponse(MenuItem menuItem) {
        return new MenuItemResponse(menuItem.getId(), menuItem.getName(), menuItem.getPrice(), menuItem.isAvailable());
    }
}
