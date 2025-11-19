package com.payment.paymentservice.controller;

import com.payment.paymentservice.dto.CreatePaymentOrderRequest;
import com.payment.paymentservice.dto.CreatePaymentOrderResponse;
import com.payment.paymentservice.service.PaymentOrderService;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentOrderController {

    private final PaymentOrderService paymentOrderService;

    @PostMapping("/orders")
    public ResponseEntity<CreatePaymentOrderResponse> createOrder(
            @Valid @RequestBody CreatePaymentOrderRequest request
    ) throws RazorpayException {
        CreatePaymentOrderResponse response = paymentOrderService.createOrder(request);
        return ResponseEntity.ok(response);
    }
}