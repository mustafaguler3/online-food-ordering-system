package com.example.FoodApp.cart.entity;

import com.example.FoodApp.auth_users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "carts")
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;
    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<CartItem> cartItems = new ArrayList<>();
    private String promoCode;
}



























