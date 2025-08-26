package com.example.FoodApp.config;

import com.example.FoodApp.auth_users.dtos.UserDTO;
import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.cart.dtos.CartDTO;
import com.example.FoodApp.cart.dtos.CartItemDTO;
import com.example.FoodApp.cart.entity.Cart;
import com.example.FoodApp.cart.entity.CartItem;
import com.example.FoodApp.category.entity.Category;
import com.example.FoodApp.menu.dtos.MenuDTO;
import com.example.FoodApp.menu.entity.Menu;
import com.example.FoodApp.order.dtos.OrderDTO;
import com.example.FoodApp.order.dtos.OrderItemDTO;
import com.example.FoodApp.order.entity.Order;
import com.example.FoodApp.order.entity.OrderItem;
import com.example.FoodApp.review.dtos.ReviewDTO;
import com.example.FoodApp.review.entity.Review;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DtoConverter {

    // ================= MENU =================
    public MenuDTO toMenuDto(Menu menu) {
        if (menu == null) return null;

        MenuDTO dto = new MenuDTO();
        dto.setId(menu.getId());
        dto.setName(menu.getName());
        dto.setDescription(menu.getDescription());
        dto.setPrice(menu.getPrice());
        dto.setImageUrl(menu.getImageUrl());
        dto.setCategoryId(menu.getCategory() != null ? menu.getCategory().getId() : 0);

        if (menu.getReviews() != null) {
            dto.setReviews(menu.getReviews().stream()
                    .map(this::toReviewDto)
                    .toList());
        }
        return dto;
    }

    public Menu toMenuEntity(MenuDTO dto, Category category) {
        if (dto == null) return null;

        Menu menu = new Menu();
        menu.setId(dto.getId());
        menu.setName(dto.getName());
        menu.setDescription(dto.getDescription());
        menu.setPrice(dto.getPrice());
        menu.setImageUrl(dto.getImageUrl());
        menu.setCategory(category);
        return menu;
    }

    // ================= REVIEW =================
    public ReviewDTO toReviewDto(Review review) {
        if (review == null) return null;

        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setMenuId(review.getMenu() != null ? review.getMenu().getId() : null);
        dto.setOrderId(review.getOrder() != null ? review.getOrder().getId() : null);
        dto.setUserName(review.getUser() != null ? review.getUser().getName() : null);
        dto.setMenuName(review.getMenu() != null ? review.getMenu().getName() : null);
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }

    public Review toReviewEntity(ReviewDTO dto, User user, Menu menu, Order order) {
        if (dto == null) return null;

        Review review = new Review();
        review.setId(dto.getId());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(dto.getCreatedAt());
        review.setUser(user);
        review.setMenu(menu);
        review.setOrder(order);
        return review;
    }

    // ================= ORDER =================
    public OrderDTO toOrderDto(Order order) {
        if (order == null) return null;

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setUser(toUserDto(order.getUser()));

        if (order.getOrderItems() != null) {
            dto.setOrderItems(order.getOrderItems().stream()
                    .map(this::toOrderItemDto)
                    .toList());
        }
        return dto;
    }

    public Order toOrderEntity(OrderDTO dto, User user, List<OrderItem> orderItems) {
        if (dto == null) return null;

        Order order = new Order();
        order.setId(dto.getId());
        order.setOrderDate(dto.getOrderDate());
        order.setTotalAmount(dto.getTotalAmount());
        order.setOrderStatus(dto.getOrderStatus());
        order.setPaymentStatus(dto.getPaymentStatus());
        order.setUser(user);
        order.setOrderItems(orderItems);
        return order;
    }

    // ================= ORDER ITEM =================
    public OrderItemDTO toOrderItemDto(OrderItem orderItem) {
        if (orderItem == null) return null;

        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setMenuId(orderItem.getMenu() != null ? orderItem.getMenu().getId() : 0);
        dto.setMenu(toMenuDto(orderItem.getMenu())); // dikkat: reviews jsonignore olabilir
        dto.setPricePerUnit(orderItem.getPricePerUnit());
        dto.setSubtotal(orderItem.getSubtotal());
        return dto;
    }

    public OrderItem toOrderItemEntity(OrderItemDTO dto, Menu menu, Order order) {
        if (dto == null) return null;

        OrderItem item = new OrderItem();
        item.setId(dto.getId());
        item.setQuantity(dto.getQuantity());
        item.setMenu(menu);
        item.setPricePerUnit(dto.getPricePerUnit());
        item.setSubtotal(dto.getSubtotal());
        item.setOrder(order);
        return item;
    }

    // ================= CART =================
    public CartDTO toCartDto(Cart cart) {
        if (cart == null) return null;

        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());

        if (cart.getCartItems() != null) {
            dto.setCartItems(cart.getCartItems().stream()
                    .map(this::toCartItemDto)
                    .toList());
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        if (cart.getCartItems() != null) {
            for (CartItem item : cart.getCartItems()) {
                totalAmount = totalAmount.add(item.getSubtotal());
            }
        }
        dto.setTotalAmount(totalAmount);

        return dto;
    }

    public CartItemDTO toCartItemDto(CartItem item) {
        if (item == null) return null;

        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setMenu(toMenuDto(item.getMenu()));
        dto.setQuantity(item.getQuantity());
        dto.setPricePerUnit(item.getPricePerUnit());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }

    // ================= USER =================
    public UserDTO toUserDto(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setAddress(user.getAddress());
        return dto;
    }
}




















