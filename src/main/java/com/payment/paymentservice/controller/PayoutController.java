package com.payment.paymentservice.controller;

import com.payment.paymentservice.dto.PayoutRequest;
import com.payment.paymentservice.dto.PayoutResponse;
import com.payment.paymentservice.service.PayoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/payouts")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutService payoutService;

    @PostMapping
    public ResponseEntity<PayoutResponse> createPayout(
            @Valid @RequestBody PayoutRequest request
    ) {
        log.info("Received payout request: userId={}, referenceId={}, amount={}",
                request.getExternalUserId(),
                request.getExternalReferenceId(),
                request.getAmount()
        );

        PayoutResponse response = payoutService.createPayout(request);

        log.info("Payout created: transactionId={}, payoutId={}",
                response.getTransactionId(),
                response.getRazorpayPayoutId()
        );

        return ResponseEntity.ok(response);
    }
}
