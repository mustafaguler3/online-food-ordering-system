package com.example.FoodApp.delivery.dto;

import com.example.FoodApp.delivery.entity.DeliveryPerson;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveryLocationDTO {
    private Long id;
    private DeliveryPersonDTO deliveryPerson;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
}
