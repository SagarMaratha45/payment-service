package com.payment.paymentservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    private String id;

    private String externalUserId;
    private double amount;
    private PaymentStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    // private String razorpayOrderId;
    // private String razorpayPaymentId;
}