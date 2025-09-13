package com.example.FoodApp.delivery.controller;

import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.delivery.dto.DeliveryLocationDTO;
import com.example.FoodApp.delivery.entity.DeliveryLocation;
import com.example.FoodApp.delivery.entity.DeliveryPerson;
import com.example.FoodApp.delivery.repository.DeliveryLocationRepository;
import com.example.FoodApp.delivery.service.DeliveryLocationService;
import com.example.FoodApp.order.entity.Order;
import com.example.FoodApp.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryLocationController {

    private final DeliveryLocationService deliveryLocationService;
    private final DeliveryLocationRepository deliveryLocationRepository;
    private final OrderRepository orderRepository;

    @PostMapping("/order/update/location")
    @PreAuthorize("hasAuthority('DELIVERY')")
    public ResponseEntity<?> updateLocation(@PathVariable Long deliveryId,
                                            @RequestParam Long lat,
                                            @RequestParam Long lng) {
        return ResponseEntity.ok(deliveryLocationService.updateLocation(deliveryId,lat,lng)
        );
    }

    @PostMapping("/order/{orderId}/location")
    @PreAuthorize("hasAuthority('DELIVERY')")
    public ResponseEntity<?> startDeliveryLocation(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        DeliveryPerson dp = order.getDeliveryPerson();
        if (dp == null) {
            return ResponseEntity.badRequest().body("No delivery person assigned");
        }

        return ResponseEntity.ok(Map.of(
                "lat", dp.getCurrentLat(),
                "lng", dp.getCurrentLng()
        ));
    }

    @GetMapping("/location/order/{orderId}/tracking")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> trackOrder(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        DeliveryPerson dp = order.getDeliveryPerson();
        if (dp == null) {
            return ResponseEntity.badRequest().body("Delivery person not yet assigned for this order");
        }

        DeliveryLocation latest = deliveryLocationService.getLatestLocation(dp.getId()).getData();
        return ResponseEntity.ok(latest);
    }
}
