package com.example.FoodApp.cart.dtos;

import com.example.FoodApp.menu.dtos.MenuDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartItemDTO {
    private long id;
    private MenuDTO menu;
    private int quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal subtotal;
}
