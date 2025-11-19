package com.payment.paymentservice.service;

import com.payment.paymentservice.dto.PayoutRequest;
import com.payment.paymentservice.dto.PayoutResponse;
import com.payment.paymentservice.model.Transaction;
import com.payment.paymentservice.model.TransactionStatus;
import com.payment.paymentservice.model.TransactionType;
import com.payment.paymentservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutService {

    private final TransactionRepository transactionRepository;

    public PayoutResponse createPayout(PayoutRequest req) {

        log.info("Creating payout: userId={}, referenceId={}, amount={}",
                req.getExternalUserId(), req.getExternalReferenceId(), req.getAmount());

        // TODO: Replace with real Razorpay Payout API when enabled for your account
        String fakePayoutId = "pout_" + UUID.randomUUID();
        log.debug("Generated fake payout id={}", fakePayoutId);

        Transaction txn = Transaction.builder()
                .type(TransactionType.PAYOUT_OUT)
                .status(TransactionStatus.SUCCESS)
                .externalUserId(req.getExternalUserId())
                .externalReferenceId(req.getExternalReferenceId())
                .amount(req.getAmount())
                .currency(req.getCurrency())
                .direction("OUT")
                .razorpayPayoutId(fakePayoutId)
                .createdAt(Instant.now())
                .build();

        transactionRepository.save(txn);

        log.info("Payout created successfully: payoutId={}, referenceId={}",
                fakePayoutId, req.getExternalReferenceId());

        return new PayoutResponse(
                txn.getId(),
                fakePayoutId,
                txn.getStatus().name()
        );
    }
}
