package com.example.FoodApp.auth_users.entity;

import com.example.FoodApp.cart.entity.Cart;
import com.example.FoodApp.order.entity.Order;
import com.example.FoodApp.payment.entity.Payment;
import com.example.FoodApp.review.entity.Review;
import com.example.FoodApp.role.entity.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
@EqualsAndHashCode(exclude = "carts")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private String phoneNumber;
    private String profileUrl;
    private String address;
    private boolean isActive;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Order> orders;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Review> reviews;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Payment> payments;
    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private Cart cart;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


















