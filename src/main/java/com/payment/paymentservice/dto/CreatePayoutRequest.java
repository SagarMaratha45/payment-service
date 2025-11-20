package com.payment.paymentservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePayoutRequest {

    @NotBlank
    private String externalUserId;

    @Min(1)
    private double amount;

    @NotBlank
    private String upiId;
}