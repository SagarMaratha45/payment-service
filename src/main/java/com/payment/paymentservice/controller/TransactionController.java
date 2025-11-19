package com.payment.paymentservice.controller;

import com.payment.paymentservice.model.Transaction;
import com.payment.paymentservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable String id) {
        return ResponseEntity.ok(transactionService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> search(
            @RequestParam(required = false) String externalUserId,
            @RequestParam(required = false) String externalReferenceId
    ) {
        if (externalReferenceId != null) {
            return ResponseEntity.ok(transactionService.findByExternalReferenceId(externalReferenceId));
        } else if (externalUserId != null) {
            return ResponseEntity.ok(transactionService.findByExternalUserId(externalUserId));
        } else {
            return ResponseEntity.ok(List.of());
        }
    }
}
