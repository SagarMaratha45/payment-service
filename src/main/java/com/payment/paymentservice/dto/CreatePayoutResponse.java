package com.payment.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatePayoutResponse {

    private String payoutId;
    private String externalUserId;
    private double amount;
    private String upiId;
    private String status;
    private String message;
}
