package com.example.FoodApp.payment.dtos;

import com.example.FoodApp.auth_users.dtos.UserDTO;
import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.enums.PaymentGateway;
import com.example.FoodApp.enums.PaymentStatus;
import com.example.FoodApp.order.dtos.OrderDTO;
import com.example.FoodApp.order.entity.Order;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    @NotNull(message = "Order id is required")
    private Long orderId;
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private String transactionId;
    //@NotNull(message = "Payment gateway is required")
    private PaymentGateway paymentGateway;
    private String failureReason;
    private LocalDateTime paymentDate;
    private boolean success;
    //@NotNull(message = "Order details cannot be null")
    private OrderDTO order;
    //@NotNull(message = "User information is required")
    private UserDTO user;
}
















