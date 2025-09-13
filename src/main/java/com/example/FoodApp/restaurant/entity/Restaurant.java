package com.example.FoodApp.restaurant.entity;

import com.example.FoodApp.menu.entity.Menu;
import com.example.FoodApp.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Menu> menus;
}



























