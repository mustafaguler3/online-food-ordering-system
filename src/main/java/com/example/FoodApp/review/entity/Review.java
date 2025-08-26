package com.example.FoodApp.review.entity;

import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.menu.entity.Menu;
import com.example.FoodApp.order.entity.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "reviews")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    private Integer rating;
    @Column(columnDefinition = "TEXT")
    private String comment;
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // hangi sipariş üzerinden yapıldı
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id",nullable = false)
    private Menu menu;
}
















