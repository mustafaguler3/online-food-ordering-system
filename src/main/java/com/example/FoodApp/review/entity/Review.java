package com.example.FoodApp.review.entity;

import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.menu.entity.Menu;
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
    @Column(name = "order_id")
    private long orderId;
    @ManyToOne
    private Menu menu;
}
















