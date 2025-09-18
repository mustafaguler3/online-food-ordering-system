package com.example.FoodApp.order.repository;

import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Page<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);
    List<Order> findByUserOrderByOrderDateDesc(User user);
    @Query("select count(DISTINCT o.user.id) from Order o")
    long countDistinctUsers();
    List<Order> findByDeliveryPerson_Id(Long deliveryId);

    @Query("select o from Order o where o.orderStatus=:status and o.deliveryPerson.id =:id")
    List<Order> findDeliveredOrdersByOrderStatusAndUserId(@Param("id") Long deliveryId,
                                                          @Param("status") OrderStatus orderStatus);

    Optional<Order> findByIdAndDeliveryPerson_Id(Long orderId, Long deliveryPersonId);
}
