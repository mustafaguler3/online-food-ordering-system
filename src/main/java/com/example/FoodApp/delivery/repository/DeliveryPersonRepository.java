package com.example.FoodApp.delivery.repository;

import com.example.FoodApp.delivery.entity.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson,Long> {
    DeliveryPerson findDeliveryPersonById(Long id);
    List<DeliveryPerson> findByHasActiveOrderFalse();
}
