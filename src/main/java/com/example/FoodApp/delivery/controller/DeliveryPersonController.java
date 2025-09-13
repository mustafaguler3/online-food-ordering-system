package com.example.FoodApp.delivery.controller;

import com.example.FoodApp.delivery.service.DeliveryPersonService;
import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.order.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryPersonController {

    private final OrderService orderService;
    private final DeliveryPersonService deliveryPersonService;

    @GetMapping("/orders/assigned")
    @PreAuthorize("hasAuthority('DELIVERY')")
    public ResponseEntity<?> getAssignedOrders(){
        return ResponseEntity.ok(orderService.getAssignedOrders());
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

























