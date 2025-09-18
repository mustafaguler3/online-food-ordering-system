package com.example.FoodApp.delivery.service;

import com.example.FoodApp.delivery.dto.UpdateLocationDTO;
import com.example.FoodApp.delivery.entity.DeliveryLocation;
import com.example.FoodApp.response.Response;

public interface DeliveryLocationService {
    Response<?> updateLocation(Long deliveryId,
                               Double lat,
                               Double lng);
    Response<DeliveryLocation> getLatestLocation(Long deliveryPersonId);
    Response<?> startLocation(Long orderId);
}
