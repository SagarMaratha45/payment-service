package com.payment.paymentservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document("payment_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrder {

    @Id
    private String id;

    // Given by caller microservice
    private String externalUserId;
    private String externalReferenceId; // e.g. INVESTMENT_ID, ORDER_ID

    private BigDecimal amount;
    private String currency;

    // Razorpay fields
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    private PaymentStatus status;

    private Instant createdAt;
    private Instant updatedAt;
}