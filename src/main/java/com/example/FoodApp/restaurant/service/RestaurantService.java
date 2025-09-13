package com.example.FoodApp.restaurant.service;

import com.example.FoodApp.response.Response;
import com.example.FoodApp.restaurant.dto.RestaurantDTO;
import com.example.FoodApp.restaurant.entity.Restaurant;

import java.util.List;

public interface RestaurantService {
    Response<List<RestaurantDTO>> getAllRestaurants();
    Response<RestaurantDTO> getRestaurantById(long restaurantId);
}
