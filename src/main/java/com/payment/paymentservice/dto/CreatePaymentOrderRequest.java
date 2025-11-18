package com.payment.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentOrderRequest {

    @NotBlank
    private String externalUserId;

    @NotBlank
    private String externalReferenceId;

    @NotNull
    @DecimalMin(value = "1.00")
    private BigDecimal amount;

    @NotBlank
    private String currency; // e.g. "INR"
}
