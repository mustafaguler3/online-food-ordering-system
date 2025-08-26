package com.example.FoodApp.order.entity;

import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.enums.PaymentStatus;
import com.example.FoodApp.payment.entity.Payment;
import com.example.FoodApp.review.entity.Review;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @OneToOne(mappedBy = "order")
    @JoinColumn(name = "payment_id")
    private Payment payment;
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> orderItems;

    // Siparişe ait yorumlar (kullanıcı sipariş sonrası yorum bırakabilir)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
}



















