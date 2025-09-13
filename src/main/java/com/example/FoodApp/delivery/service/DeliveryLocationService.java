package com.example.FoodApp.delivery.service;

import com.example.FoodApp.delivery.entity.DeliveryLocation;
import com.example.FoodApp.response.Response;

public interface DeliveryLocationService {
    Response<?> updateLocation(Long deliveryPersonId,double lat,double lng);
    Response<DeliveryLocation> getLatestLocation(Long deliveryPersonId);
}
