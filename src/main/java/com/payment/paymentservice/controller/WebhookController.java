package com.payment.paymentservice.controller;

import com.payment.paymentservice.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(name = "X-Razorpay-Signature", required = false) String signature
    ) {
        webhookService.handleWebhook(payload, signature);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
