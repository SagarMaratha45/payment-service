package com.payment.paymentservice.repository;

import com.payment.paymentservice.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByExternalReferenceId(String externalReferenceId);

    List<Transaction> findByExternalUserId(String externalUserId);
}