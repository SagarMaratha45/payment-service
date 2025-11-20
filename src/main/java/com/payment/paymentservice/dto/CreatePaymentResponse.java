package com.payment.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatePaymentResponse {

    private String paymentId;
    private String externalUserId;
    private double amount;
    private String status;
    private String message;
}