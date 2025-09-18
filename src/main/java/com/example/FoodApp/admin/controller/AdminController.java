package com.example.FoodApp.admin.controller;

import com.example.FoodApp.auth_users.services.UserService;
import com.example.FoodApp.delivery.service.DeliveryPersonService;
import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.order.dtos.OrderDTO;
import com.example.FoodApp.order.services.OrderService;
import com.example.FoodApp.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderService orderService;
    private final UserService userService;
    private final DeliveryPersonService deliveryPersonService;

    @GetMapping("/deliveries")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> findUsersByRoleDelivery(){
        return ResponseEntity.ok(deliveryPersonService.findAllDeliveries());
    }

    @PostMapping("/orders/{orderId}/assign/auto")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> autoAssignDeliveryToPerson(@PathVariable Long orderId){
        return ResponseEntity.ok(orderService.autoAssignDeliveryPerson(orderId));
    }

    @PostMapping("/orders/{orderId}/assign/manuel")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> manuelAssignDeliveryToPerson(@PathVariable Long orderId,
                                                          @RequestParam Long deliveryId){
        return ResponseEntity.ok(orderService.manuelAssignDeliveryPerson(orderId,deliveryId));
    }


}
