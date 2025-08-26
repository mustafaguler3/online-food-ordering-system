package com.example.FoodApp.order.entity;

import com.example.FoodApp.menu.entity.Menu;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "order_items")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;
    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;
    private int quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal subtotal;
}



















