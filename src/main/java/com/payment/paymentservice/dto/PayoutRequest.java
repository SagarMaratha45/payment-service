package com.payment.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayoutRequest {

    @NotBlank
    private String externalUserId;

    @NotBlank
    private String externalReferenceId;

    @NotNull
    @DecimalMin("1.00")
    private BigDecimal amount;

    @NotBlank
    private String currency; // "INR"

    @NotBlank
    private String mode;     // e.g. "UPI", "BANK_TRANSFER"

    @NotNull
    private Beneficiary beneficiary;

    @Data
    public static class Beneficiary {
        @NotBlank
        private String name;

        @NotBlank
        private String upiIdOrAccount; // depends on mode
    }
}
