package com.payment.paymentservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.payment.paymentservice.model.PaymentOrder;

import java.util.Optional;

public interface PaymentOrderRepository extends MongoRepository<PaymentOrder, String> {

    Optional<PaymentOrder> findByRazorpayOrderId(String razorpayOrderId);
}