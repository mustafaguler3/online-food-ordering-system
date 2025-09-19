package com.example.FoodApp.config;

import com.example.FoodApp.auth_users.dtos.AddressDTO;
import com.example.FoodApp.auth_users.dtos.UserDTO;
import com.example.FoodApp.auth_users.entity.Address;
import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.delivery.dto.DeliveryPersonDTO;
import com.example.FoodApp.delivery.entity.DeliveryPerson;
import com.example.FoodApp.menu.dtos.MenuDTO;
import com.example.FoodApp.order.dtos.OrderDTO;
import com.example.FoodApp.order.dtos.OrderItemDTO;
import com.example.FoodApp.order.entity.Order;
import com.example.FoodApp.order.entity.OrderItem;
import com.example.FoodApp.restaurant.dto.RestaurantDTO;
import com.example.FoodApp.restaurant.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {
    public OrderDTO toOrderDto(Order order) {
        if (order == null) return null;

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderCode(order.getOrderCode());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());

        // User
        dto.setUser(toUserDto(order.getUser()));

        // Delivery Person
        dto.setDeliveryPerson(toDeliveryPersonDto(order.getDeliveryPerson()));

        // Restaurant
        dto.setRestaurant(toRestaurantDto(order.getRestaurant()));

        // Delivery Address
        dto.setDeliveryAddress(toAddressDto(order.getDeliveryAddress()));

        // Order Items
        if (order.getOrderItems() != null) {
            dto.setOrderItems(order.getOrderItems()
                    .stream()
                    .map(this::toOrderItemDto)
                    .toList());
        }

        return dto;
    }

    public Order toOrderEntity(OrderDTO dto, User user, List<OrderItem> orderItems,
                               Restaurant restaurant, Address address, DeliveryPerson dp) {
        if (dto == null) return null;

        Order order = new Order();
        order.setId(dto.getId());
        order.setOrderCode(dto.getOrderCode());
        order.setOrderDate(dto.getOrderDate());
        order.setTotalAmount(dto.getTotalAmount());
        order.setOrderStatus(dto.getOrderStatus());
        order.setPaymentStatus(dto.getPaymentStatus());

        order.setUser(user);
        order.setOrderItems(orderItems);
        order.setRestaurant(restaurant);
        order.setDeliveryAddress(address);
        order.setDeliveryPerson(dp);

        return order;
    }

    // --- Yardımcı DTO Map'leri ---

    private UserDTO toUserDto(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setProfileUrl(user.getProfileUrl());
        return dto;
    }

    private DeliveryPersonDTO toDeliveryPersonDto(DeliveryPerson dp) {
        if (dp == null) return null;
        DeliveryPersonDTO dto = new DeliveryPersonDTO();
        dto.setId(dp.getId());
        dto.setHasActiveOrder(dp.isHasActiveOrder());
        return dto;
    }

    private RestaurantDTO toRestaurantDto(Restaurant restaurant) {
        if (restaurant == null) return null;
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setLatitude(restaurant.getLatitude());
        dto.setLongitude(restaurant.getLongitude());
        return dto;
    }

    private AddressDTO toAddressDto(Address address) {
        if (address == null) return null;
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setPostalCode(address.getPostalCode());
        dto.setLatitude(address.getLatitude());
        dto.setLongitude(address.getLongitude());
        return dto;
    }
    public List<OrderDTO> toOrderDtoList(List<Order> orders) {
        if (orders == null) return null;

        return orders.stream()
                .map(this::toOrderDto)
                .toList();
    }

    private OrderItemDTO toOrderItemDto(OrderItem item) {
        if (item == null) return null;
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getSubtotal());

        if (item.getMenu() != null) {
            MenuDTO menuDTO = new MenuDTO();
            menuDTO.setId(item.getMenu().getId());
            menuDTO.setName(item.getMenu().getName());
            menuDTO.setImageUrl(item.getMenu().getImageUrl());
            menuDTO.setPrice(item.getMenu().getPrice());
            dto.setMenu(menuDTO);
        }

        return dto;
    }
}
