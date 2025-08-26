package com.example.FoodApp.order.dtos;

import com.example.FoodApp.auth_users.dtos.UserDTO;
import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.enums.PaymentStatus;
import com.example.FoodApp.order.entity.OrderItem;
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

    private long id;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private UserDTO user;
    @JsonIgnore
    private List<OrderItemDTO> orderItems;

}


















