package com.payment.paymentservice.service;

import com.payment.paymentservice.model.Transaction;
import com.payment.paymentservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Transaction getById(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + id));
    }

    public List<Transaction> findByExternalReferenceId(String externalReferenceId) {
        return transactionRepository.findByExternalReferenceId(externalReferenceId);
    }

    public List<Transaction> findByExternalUserId(String externalUserId) {
        return transactionRepository.findByExternalUserId(externalUserId);
    }
}

