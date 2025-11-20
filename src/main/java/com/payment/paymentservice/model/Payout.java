package com.payment.paymentservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "payouts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payout {

    @Id
    private String id;

    private String externalUserId;
    private double amount;
    private String upiId;
    private PayoutStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
