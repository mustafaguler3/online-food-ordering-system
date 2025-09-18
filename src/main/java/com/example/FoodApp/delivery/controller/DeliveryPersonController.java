package com.example.FoodApp.delivery.controller;

import com.example.FoodApp.auth_users.entity.Address;
import com.example.FoodApp.delivery.entity.DeliveryPerson;
import com.example.FoodApp.delivery.service.DeliveryPersonService;
import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.order.entity.Order;
import com.example.FoodApp.order.repository.OrderRepository;
import com.example.FoodApp.order.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryPersonController {

    private final OrderService orderService;
    private final DeliveryPersonService deliveryPersonService;
    private final OrderRepository orderRepository;

    @GetMapping("/orders/assigned")
    @PreAuthorize("hasAuthority('DELIVERY')")
    public ResponseEntity<?> getAssignedOrders(){
        return ResponseEntity.ok(orderService.getAssignedOrders());
    }

    @GetMapping("/orders/assigned/{orderId}")
    @PreAuthorize("hasAuthority('DELIVERY')")
    public ResponseEntity<?> findAssignedOrder(
            @PathVariable Long orderId
    ){
        return ResponseEntity.ok(orderService.getAssignedOrderById(orderId));
    }

    @GetMapping("/order/{orderId}/location")
    public ResponseEntity<?> getDeliveryLocation(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        DeliveryPerson dp = order.getDeliveryPerson();
        if (dp == null) {
            return ResponseEntity.badRequest().body("No delivery person assigned");
        }

        // Kurye konumu
        Map<String, Double> courierLoc = Map.of(
                "lat", dp.getCurrentLat(),
                "lng", dp.getCurrentLng()
        );

        // Müşteri adresi (varsayılan ilk adresi alıyoruz)
        Address customerAddress = order.getUser().getAddresses().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Customer address not found"));

        Map<String, Double> customerLoc = Map.of(
                "lat", customerAddress.getLatitude(),
                "lng", customerAddress.getLongitude()
        );

        // Restaurant konumu
        Map<String, Double> restaurantLoc = Map.of(
                "lat", order.getRestaurant().getLatitude(),
                "lng", order.getRestaurant().getLongitude()
        );

        Map<String, Object> response = Map.of(
                "courier", courierLoc,
                "customer", customerLoc,
                "restaurant", restaurantLoc
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/orders/{orderId}/status")
    @PreAuthorize("hasAuthority('DELIVERY')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId,
                                                    @RequestParam String status){
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId,status));
    }

    @GetMapping("/orders/delivered")
    @PreAuthorize("hasAuthority('DELIVERY')")
    public ResponseEntity<?> deliveredOrders(){
        return ResponseEntity.ok(orderService.findDeliveredOrders());
    }

}

























