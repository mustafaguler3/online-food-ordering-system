package com.example.FoodApp.delivery.service;

import com.example.FoodApp.delivery.dto.DeliveryPersonDTO;
import com.example.FoodApp.order.entity.Order;
import com.example.FoodApp.response.Response;

import java.util.List;

public interface DeliveryPersonService {
    Response<List<DeliveryPersonDTO>> findAllDeliveries();
}
