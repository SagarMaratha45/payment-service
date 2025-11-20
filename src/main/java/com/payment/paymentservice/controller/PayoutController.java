package com.payment.paymentservice.controller;

import com.payment.paymentservice.dto.CreatePayoutRequest;
import com.payment.paymentservice.dto.CreatePayoutResponse;
import com.payment.paymentservice.service.PayoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/payouts")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutService payoutService;

    @PostMapping
    public ResponseEntity<CreatePayoutResponse> createPayout(@Valid @RequestBody CreatePayoutRequest request) {
        log.info("Received payout create request: userId={}, amount={}, upiId={}",
                request.getExternalUserId(), request.getAmount(), request.getUpiId());

        CreatePayoutResponse response = payoutService.createPayout(request);

        log.info("Payout response: id={}, status={}", response.getPayoutId(), response.getStatus());
        return ResponseEntity.ok(response);
    }
}