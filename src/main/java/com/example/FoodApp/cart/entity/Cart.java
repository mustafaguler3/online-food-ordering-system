package com.example.FoodApp.cart.entity;

import com.example.FoodApp.auth_users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Table(name = "carts")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "user")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    private String promoCode;
}



























