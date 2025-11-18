package com.payment.paymentservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document("transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    private String id;

    private TransactionType type;
    private TransactionStatus status;

    private String externalUserId;
    private String externalReferenceId;

    private BigDecimal amount;
    private String currency;

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpayPayoutId;

    private String direction;   // "IN" or "OUT"

    private Instant createdAt;
}