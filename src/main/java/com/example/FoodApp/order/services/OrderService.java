package com.example.FoodApp.order.services;

import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.order.dtos.OrderDTO;
import com.example.FoodApp.order.dtos.OrderItemDTO;
import com.example.FoodApp.response.Response;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    Response<?> placeOrderFromCart();
    Response<OrderDTO> getOrderById(Long id);
    Response<Page<OrderDTO>> getAllOrders(OrderStatus orderStatus,int page,int size);
    Response<List<OrderDTO>> getOrdersOfUser();
    Response<OrderItemDTO> getOrderItemById(Long orderItemId);
    Response<OrderDTO> updateOrderStatus(OrderDTO orderDTO);
    Response<Long> countUniqueCustomers();
    Response<?> autoAssignDeliveryPerson(Long orderId);
    Response<?> manuelAssignDeliveryPerson(Long orderId,Long deliveryId);
    Response<List<OrderDTO>> getAssignedOrders();
    Response<OrderDTO> updateOrderStatus(Long orderId,String status);
    Response<OrderDTO> getAssignedOrderById(Long orderId);
    Response<List<OrderDTO>> findDeliveredOrders();
}
