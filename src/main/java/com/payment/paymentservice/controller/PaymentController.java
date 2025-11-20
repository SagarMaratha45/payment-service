package com.payment.paymentservice.controller;

import com.payment.paymentservice.dto.CreatePaymentRequest;
import com.payment.paymentservice.dto.CreatePaymentResponse;
import com.payment.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @Slf4j
@RestController
@RequestMapping("/api/v1/deposit")
// @RequiredArgsConstructor
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    // Constructor to replace @RequiredArgsConstructor
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<CreatePaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        log.info("Received payment create request: userId={}, amount={}",
                request.getExternalUserId(), request.getAmount());
        CreatePaymentResponse response = paymentService.createPayment(request);
        log.info("Payment response: id={}, status={}", response.getPaymentId(), response.getStatus());
        return ResponseEntity.ok(response);
    }
}
