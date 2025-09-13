package com.example.FoodApp.auth_users.repository;

import com.example.FoodApp.auth_users.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u JOIN u.roles r " +
            "WHERE r.name = 'DELIVERY' " +
            "AND u.isActive = true " +
            "AND NOT EXISTS (" +
            "   SELECT o FROM Order o WHERE o.user = u AND o.orderStatus IN ('ON_THE_WAY', 'ASSIGNED')" +
            ")")
    List<User> findAvailableDeliveries();

    List<User> findByRoles_Name(String roleName);
}
