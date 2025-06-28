package com.example.FoodApp.order.controller;

import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.order.dtos.OrderDTO;
import com.example.FoodApp.order.dtos.OrderItemDTO;
import com.example.FoodApp.order.services.OrderService;
import com.example.FoodApp.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Response<?>> checkout(){
        return ResponseEntity.ok(orderService.placeOrderFromCart());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<OrderDTO>> getOrderById(@PathVariable long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<Response<List<OrderDTO>>> getMyOrders() {
        return ResponseEntity.ok(orderService.getOrdersOfUser());
    }

    @GetMapping("/order-item/{orderItemId}")
    public ResponseEntity<Response<OrderItemDTO>> getOrderItemById(@PathVariable long orderItemId) {
        return ResponseEntity.ok(orderService.getOrderItemById(orderItemId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<?>> getAllOrders(@RequestParam(required = false)OrderStatus orderStatus,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "100") int size){
        return ResponseEntity.ok(orderService.getAllOrders(orderStatus,page,size));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<?>> updateOrderStatus(@RequestBody OrderDTO orderDTO){
        return ResponseEntity.ok(orderService.updateOrderStatus(orderDTO));
    }

    @GetMapping("/unique-customers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<?>> countUniqueCustomers(){
        return ResponseEntity.ok(orderService.countUniqueCustomers());
    }
}

















