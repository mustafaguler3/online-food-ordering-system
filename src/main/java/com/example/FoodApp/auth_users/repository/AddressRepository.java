package com.example.FoodApp.auth_users.repository;

import com.example.FoodApp.auth_users.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,Long> {
    Address findAddressByUserId(Long userId);
}
