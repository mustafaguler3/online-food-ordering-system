package com.example.FoodApp.delivery.dto;

import lombok.Data;

@Data
public class UpdateLocationDTO {
    private Long deliveryId;
    private Double lat;
    private Double lng;
}
