package com.example.FoodApp.delivery.dto;

import com.example.FoodApp.auth_users.dtos.UserDTO;
import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.delivery.entity.DeliveryLocation;
import com.example.FoodApp.order.dtos.OrderDTO;
import com.example.FoodApp.order.entity.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DeliveryPersonDTO {
    private Long id;
    private UserDTO user;
    private String vehicleType;
    @JsonIgnore
    private List<OrderDTO> orders;
    @JsonIgnore
    private List<DeliveryLocationDTO> deliveryLocations;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private String licenseNumber;
    private boolean online;
    private Double currentLat;
    private Double currentLng;
    private boolean hasActiveOrder;
}
