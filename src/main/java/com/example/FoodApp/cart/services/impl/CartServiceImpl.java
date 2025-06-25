package com.example.FoodApp.cart.services.impl;

import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.auth_users.services.UserService;
import com.example.FoodApp.cart.dtos.CartDTO;
import com.example.FoodApp.cart.entity.Cart;
import com.example.FoodApp.cart.entity.CartItem;
import com.example.FoodApp.cart.repository.CartItemRepository;
import com.example.FoodApp.cart.repository.CartRepository;
import com.example.FoodApp.cart.services.CartService;
import com.example.FoodApp.exceptions.NotFoundException;
import com.example.FoodApp.menu.entity.Menu;
import com.example.FoodApp.menu.repository.MenuRepository;
import com.example.FoodApp.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Response<?> addItemToCart(CartDTO cartDTO) {
        Long menuId = cartDTO.getMenuId();
        int quantity = cartDTO.getQuantity();
        User user = userService.getCurrentLoggedInUser();
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new NotFoundException("Menu item not found"));
        Cart cart = cartRepository.findByUser_Id(user.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setCartItems(new ArrayList<>());
            return cartRepository.save(newCart);
        });
        // Check if the item is already in the cart
        Optional<CartItem> optionalCartItem =
                cart.getCartItems()
                        .stream()
                        .filter(cartItem -> cartItem.getMenu().getId().equals(menuId))
                        .findFirst();

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setSubtotal(cartItem.getPricePerUnit().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            cartItemRepository.save(cartItem);
        }else {
            // if not present, and add it
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .menu(menu)
                    .quantity(quantity)
                    .pricePerUnit(menu.getPrice())
                    .subtotal(menu.getPrice().multiply(BigDecimal.valueOf(quantity)))
                    .build();

            cart.getCartItems().add(newCartItem);
            cartItemRepository.save(newCartItem);
        }


        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Item added to cart successfully")
                .build();
    }

    @Override
    public Response<?> incrementItem(Long menuId) {
        User user = userService.getCurrentLoggedInUser();

        Cart cart = cartRepository.findByUser_Id(user.getId()).orElseThrow(() -> new NotFoundException("Cart not found"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getMenu().getId().equals(menuId))
                .findFirst().orElseThrow(() -> new NotFoundException("Menu not found in cart"));

        int newQuantity = cartItem.getQuantity() + 1;

        cartItem.setQuantity(newQuantity);
        cartItem.setSubtotal(cartItem.getPricePerUnit().multiply(BigDecimal.valueOf(newQuantity)));

        cartItemRepository.save(cartItem);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Item quantity incremented successfully")
                .build();
    }

    @Override
    public Response<?> decrementItem(Long menuId) {
        User user = userService.getCurrentLoggedInUser();

        Cart cart = cartRepository.findByUser_Id(user.getId()).orElseThrow(() -> new NotFoundException("Cart not found"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getMenu().getId().equals(menuId))
                .findFirst().orElseThrow(() -> new NotFoundException("Menu not found in cart"));

        int newQuantity = cartItem.getQuantity() - 1;

        if (newQuantity > 0) {
            cartItem.setQuantity(newQuantity);
            cartItem.setSubtotal(cartItem.getPricePerUnit().multiply(BigDecimal.valueOf(newQuantity)));
            cartItemRepository.save(cartItem);
        }else {
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        }

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Item quantity updated successfully")
                .build();
    }

    @Override
    public Response<?> removeItem(Long cartItemId) {
        User user = userService.getCurrentLoggedInUser();
        Cart cart = cartRepository.findByUser_Id(user.getId()).orElseThrow(() -> new NotFoundException("Cart not found"));
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new NotFoundException("Cart item not found"));

        if (!cart.getCartItems().contains(cartItem)) {
            throw new NotFoundException("Cart item does not belong to this user's cart");
        }
        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Item removed from cart successfully")
                .build();
    }

    @Override
    public Response<CartDTO> getShoppingCart() {
        User user = userService.getCurrentLoggedInUser();
        Cart cart = cartRepository.findByUser_Id(user.getId()).orElseThrow(() -> new NotFoundException("Cart not found"));
        List<CartItem> cartItems = cart.getCartItems();
        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                totalAmount = totalAmount.add(item.getSubtotal());
            }
        }
        cartDTO.setTotalAmount(totalAmount);

        // remove the review from the response
        if (cartDTO.getCartItems() != null) {
            cartDTO.getCartItems().forEach(item -> item.getMenu().setReviews(null));
        }
        return Response.<CartDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Shopping cart retrieved successfully")
                .build();
    }

    @Override
    public Response<?> clearShoppingCart() {
        User user = userService.getCurrentLoggedInUser();

        Cart cart = cartRepository.findByUser_Id(user.getId()).orElseThrow(() -> new NotFoundException("Cart not found"));

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();

        cartRepository.save(cart);

        return Response.<CartDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Shopping cart cleared successfully")
                .build();
    }
}





















