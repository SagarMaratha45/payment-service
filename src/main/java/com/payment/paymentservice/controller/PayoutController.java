package com.payment.paymentservice.controller;

import com.payment.paymentservice.dto.PayoutRequest;
import com.payment.paymentservice.dto.PayoutResponse;
import com.payment.paymentservice.service.PayoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payouts")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutService payoutService;

    @PostMapping
    public ResponseEntity<PayoutResponse> createPayout(
            @Valid @RequestBody PayoutRequest request
    ) {
        PayoutResponse response = payoutService.createPayout(request);
        return ResponseEntity.ok(response);
    }
}
