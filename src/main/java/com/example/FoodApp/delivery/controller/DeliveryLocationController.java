package com.example.FoodApp.delivery.controller;

import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.delivery.dto.DeliveryLocationDTO;
import com.example.FoodApp.delivery.dto.UpdateLocationDTO;
import com.example.FoodApp.delivery.entity.DeliveryLocation;
import com.example.FoodApp.delivery.entity.DeliveryPerson;
import com.example.FoodApp.delivery.repository.DeliveryLocationRepository;
import com.example.FoodApp.delivery.repository.DeliveryPersonRepository;
import com.example.FoodApp.delivery.service.DeliveryLocationService;
import com.example.FoodApp.order.entity.Order;
import com.example.FoodApp.order.repository.OrderRepository;
import com.example.FoodApp.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate messagingTemplate;
    private final DeliveryPersonRepository deliveryPersonRepository;

    @MessageMapping("/updateLocation")
    public void updateLocationSocket(UpdateLocationDTO location) {
        messagingTemplate.convertAndSend("/topic/delivery/" + location.getDeliveryId(),location);
    }
    @PostMapping("/order/update/location")
    //@PreAuthorize("hasAuthority('DELIVERY')")
    public ResponseEntity<?> updateLocation(
            @RequestParam Long deliveryId,
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {
        return ResponseEntity.ok(deliveryLocationService.updateLocation(deliveryId,lat,lng)
        );
    }

    @PostMapping("/order/{orderId}/location")
    @PreAuthorize("hasAuthority('DELIVERY')")
    public ResponseEntity<?> startDeliveryLocation(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryLocationService.startLocation(orderId));
    }
}
