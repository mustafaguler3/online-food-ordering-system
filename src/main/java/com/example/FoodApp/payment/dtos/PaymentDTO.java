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
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDTO {
    private long orderId;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private PaymentGateway paymentGateway;
    private String failureReason;
    private LocalDateTime paymentDate;
    private boolean success;
    private OrderDTO order;
    private UserDTO user;
}
















