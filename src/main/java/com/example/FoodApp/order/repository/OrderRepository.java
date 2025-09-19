package com.example.FoodApp.order.repository;

import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.enums.PaymentStatus;
import com.example.FoodApp.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    long countByDeliveryPerson_IdAndOrderStatus(Long deliveryPersonId, OrderStatus status);
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) " +
            "FROM Order o " +
            "WHERE o.deliveryPerson.id = :deliveryPersonId " +
            "AND o.paymentStatus = :paymentStatus " +
            "AND o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal sumEarningsByDeliveryPersonAndDateRange(
            @Param("deliveryPersonId") Long deliveryPersonId,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate

    );
}
