package com.example.FoodApp.payment.service;

import com.example.FoodApp.payment.dtos.PaymentDTO;
import com.example.FoodApp.response.Response;

import java.util.List;

public interface PaymentService {
    Response<?> initializePayment(PaymentDTO paymentDTO);
    void updatePaymentForOrder(PaymentDTO paymentDTO);
    Response<List<PaymentDTO>> getAllPayments();
    Response<PaymentDTO> getPaymentById(Long paymentId);
}
