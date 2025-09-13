package com.example.FoodApp.delivery.repository;

import com.example.FoodApp.delivery.entity.DeliveryLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryLocationRepository extends JpaRepository<DeliveryLocation,Long> {
    Optional<DeliveryLocation> findFirstByDeliveryPersonIdOrderByTimestampDesc(Long deliveryPersonId);
}
