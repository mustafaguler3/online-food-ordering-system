package com.example.FoodApp.cart.entity;

import com.example.FoodApp.menu.entity.Menu;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;
    private int quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal subtotal;
}


























