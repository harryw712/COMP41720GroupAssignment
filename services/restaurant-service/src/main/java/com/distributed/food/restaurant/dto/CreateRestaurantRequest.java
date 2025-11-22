package com.distributed.food.restaurant.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateRestaurantRequest {

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
