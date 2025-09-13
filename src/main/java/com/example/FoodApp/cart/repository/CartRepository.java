package com.example.FoodApp.cart.repository;

import com.example.FoodApp.cart.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {

    //@EntityGraph(attributePaths = "cartItems")
    Optional<Cart> findByUser_Id(Long userId);
}
