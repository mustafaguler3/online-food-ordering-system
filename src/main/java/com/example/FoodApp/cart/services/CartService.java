package com.example.FoodApp.cart.services;

import com.example.FoodApp.cart.dtos.CartDTO;
import com.example.FoodApp.response.Response;

public interface CartService {
    Response<?> addItemToCart(CartDTO cartDTO);
    Response<?> incrementItem(Long menuId);
    Response<?> decrementItem(Long menuId);
    Response<?> removeItem(Long cartItemId);
    Response<CartDTO> getShoppingCart();
    Response<?> clearShoppingCart();
}
