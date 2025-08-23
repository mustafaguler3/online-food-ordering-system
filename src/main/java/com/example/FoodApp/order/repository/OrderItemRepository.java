package com.example.FoodApp.order.repository;

import com.example.FoodApp.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    @Query("select case when count(oi) > 0 then true else false end " +
            "from OrderItem oi " +
            "where oi.order.id = :orderId and oi.menu.id = :menuId")
    boolean existsByOrderIdAndMenuId(@Param("orderId") long orderId,
                                     @Param("menuId") long menuId);
}
