package com.example.FoodApp.restaurant.dto;

import com.example.FoodApp.menu.dtos.MenuDTO;
import com.example.FoodApp.menu.entity.Menu;
import com.example.FoodApp.order.dtos.OrderDTO;
import com.example.FoodApp.order.entity.Order;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Data
public class RestaurantDTO {
    private long id;
    private String name;
    private String address;
    private String phone;
    private String logoUrl;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private String openingHours;
    private Double rating = 0.0;
    private List<OrderDTO> orders;
    private List<MenuDTO> menus;
}
