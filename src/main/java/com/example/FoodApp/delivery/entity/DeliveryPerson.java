package com.example.FoodApp.delivery.entity;

import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "delivery_persons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String vehicleType;
    @OneToMany(mappedBy = "deliveryPerson")
    private List<Order> orders;
    @OneToMany(mappedBy = "deliveryPerson",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<DeliveryLocation> deliveryLocations;

    private String licenseNumber;
    private boolean online;
    private Double currentLat;
    private Double currentLng;
    private boolean hasActiveOrder;
}
