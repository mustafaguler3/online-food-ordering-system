package com.example.FoodApp.order.dtos;

import com.example.FoodApp.menu.dtos.MenuDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemDTO {
    private long id;
    private int quantity;
    private long menuId;
    private MenuDTO menu;
    private BigDecimal pricePerUnit;
    private BigDecimal subtotal;
}



















