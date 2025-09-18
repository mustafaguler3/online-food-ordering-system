package com.example.FoodApp.order.dtos;

import com.example.FoodApp.auth_users.dtos.UserDTO;
import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.delivery.dto.DeliveryPersonDTO;
import com.example.FoodApp.delivery.entity.DeliveryPerson;
import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.enums.PaymentStatus;
import com.example.FoodApp.order.entity.OrderItem;
import com.example.FoodApp.restaurant.dto.RestaurantDTO;
import com.example.FoodApp.restaurant.entity.Restaurant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDTO {

    private Long id;
    private LocalDateTime orderDate;
    private DeliveryPersonDTO deliveryPerson;
    private String orderCode;
    private BigDecimal totalAmount;
    private RestaurantDTO restaurant;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private UserDTO user;
    private List<OrderItemDTO> orderItems;

}


















