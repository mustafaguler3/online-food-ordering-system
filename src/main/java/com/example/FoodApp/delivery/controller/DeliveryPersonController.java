package com.example.FoodApp.delivery.controller;

import com.example.FoodApp.auth_users.entity.Address;
import com.example.FoodApp.delivery.dto.DashboardDTO;
import com.example.FoodApp.delivery.entity.DeliveryPerson;
import com.example.FoodApp.delivery.service.DeliveryPersonService;
import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.order.entity.Order;
import com.example.FoodApp.order.repository.OrderRepository;
import com.example.FoodApp.order.services.OrderService;
import com.example.FoodApp.response.Response;
import com.example.FoodApp.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryPersonController {

    private final OrderService orderService;
    private final DeliveryPersonService deliveryPersonService;
    private final OrderRepository orderRepository;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('DELIVERY')")
    public ResponseEntity<?> getDashboard(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser currentUser = (AuthUser) authentication.getPrincipal();

        Long deliveryPersonId = currentUser.getUser().getDeliveryPerson().getId();

        Response<DashboardDTO> response = orderService.getDashboard(deliveryPersonId);

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response);
    }

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
        Map<String, Double> courierLoc = Map.of(
                "lat", dp.getCurrentLat(),
                "lng", dp.getCurrentLng()
        );
        Address customerAddress = order.getUser().getAddresses().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Customer address not found"));

        Map<String, Double> customerLoc = Map.of(
                "lat", customerAddress.getLatitude(),
                "lng", customerAddress.getLongitude()
        );
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

























