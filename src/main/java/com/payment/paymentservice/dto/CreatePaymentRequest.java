package com.payment.paymentservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePaymentRequest {

    @NotBlank
    private String externalUserId;

    @Min(1)
    private double amount;   // in currency units
}