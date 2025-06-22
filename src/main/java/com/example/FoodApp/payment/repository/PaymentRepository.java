package com.example.FoodApp.payment.repository;

import com.example.FoodApp.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
}
