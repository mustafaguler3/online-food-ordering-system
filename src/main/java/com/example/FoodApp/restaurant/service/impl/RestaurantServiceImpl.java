package com.example.FoodApp.restaurant.service.impl;

import com.example.FoodApp.response.Response;
import com.example.FoodApp.restaurant.dto.RestaurantDTO;
import com.example.FoodApp.restaurant.entity.Restaurant;
import com.example.FoodApp.restaurant.repository.RestaurantRepository;
import com.example.FoodApp.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    @Override
    public Response<List<RestaurantDTO>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();

        if (restaurants.isEmpty()) {
            return Response.<List<RestaurantDTO>>builder()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message("No restaurant any")
                    .build();
        }

        List<RestaurantDTO>
                restaurantDTOS =
                restaurants
                        .stream()
                        .map(restaurant ->
                modelMapper.map(restaurant,RestaurantDTO.class)).toList();

        return Response.<List<RestaurantDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .data(restaurantDTOS)
                .build();
    }

    @Override
    public Response<RestaurantDTO> getRestaurantById(long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant Not Found"));

        RestaurantDTO restaurantDTO = modelMapper.map(restaurant,RestaurantDTO.class);

        return Response.<RestaurantDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Restaurant successfully got")
                .data(restaurantDTO)
                .build();
    }
}



























