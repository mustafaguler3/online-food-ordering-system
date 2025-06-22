package com.example.FoodApp.cart.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartDTO {
    private long id;
    private List<CartItemDTO> cartItems;
    private long menuId;
    private int quantity;
    private BigDecimal totalAmount;
}
