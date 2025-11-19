package com.payment.paymentservice.controller;

import com.payment.paymentservice.dto.CreatePaymentOrderRequest;
import com.payment.paymentservice.dto.CreatePaymentOrderResponse;
import com.payment.paymentservice.service.PaymentOrderService;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentOrderController {

    private final PaymentOrderService paymentOrderService;

    @PostMapping("/orders")
    public ResponseEntity<CreatePaymentOrderResponse> createOrder(
            @Valid @RequestBody CreatePaymentOrderRequest request
    ) throws RazorpayException {
        log.info("Received create payment order request: userId={}, referenceId={}, amount={}, currency={}",
                request.getExternalUserId(),
                request.getExternalReferenceId(),
                request.getAmount(),
                request.getCurrency()
        );
        CreatePaymentOrderResponse response = paymentOrderService.createOrder(request);

        log.info("Payment order created successfully: paymentOrderId={}, razorpayOrderId={}",
                response.getPaymentOrderId(),
                response.getRazorpayOrderId()
        );
        return ResponseEntity.ok(response);
    }
}